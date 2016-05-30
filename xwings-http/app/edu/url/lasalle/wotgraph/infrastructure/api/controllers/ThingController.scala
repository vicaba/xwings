package edu.url.lasalle.wotgraph.infrastructure.api.controllers

import edu.url.lasalle.wotgraph.application.exceptions.{ClientFormatException, CoherenceException, DatabaseException}
import edu.url.lasalle.wotgraph.application.usecase.{CreateThing, GetThings, ThingUseCase}
import edu.url.lasalle.wotgraph.domain.entity.thing.Thing
import edu.url.lasalle.wotgraph.domain.entity.thing.action.{ExecutionFailure, ExecutionSuccess}
import play.api.libs.json._
import play.api.mvc._
import scaldi.Injectable._
import edu.url.lasalle.wotgraph.infrastructure.DependencyInjector._
import edu.url.lasalle.wotgraph.infrastructure.api.serializers.json.ThingSerializer
import edu.url.lasalle.wotgraph.infrastructure.api.serializers.json.ThingMinifiedSerializer
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.dto.Implicits._
import edu.url.lasalle.wotgraph.infrastructure.serializers.json.{ThingSerializer => BaseThingSerializer}
import play.api.libs.EventSource
import play.api.libs.iteratee.Enumeratee

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ThingController extends Controller with PredefJsonMessages {

  lazy val thingUseCase: ThingUseCase = inject[ThingUseCase](identified by 'ThingUseCase)

  def createThing = Action.async(parse.json) { request =>

      val res = request.body.validate[CreateThing]
      res match {
        case JsSuccess(createThingDto, _) =>
          val f = thingUseCase.createThing(createThingDto)
          f.map { t =>
            Created(Json.obj(BaseThingSerializer.IdKey -> t._id))
          } recover {
            case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
            case e: CoherenceException => UnprocessableEntity(Json.obj(MessageKey -> e.msg))
          }
        case e: JsError => Future.successful(BadRequest(e.toString))
      }
  }

  def updateThing(id: String) = Action.async(parse.json) { request =>

    val res = request.body.validate[CreateThing]
    res match {
      case JsSuccess(createThingDto, _) =>
        val f = thingUseCase.updateThing(id, createThingDto)
        f.map {
          case Some(t) => Ok(Json.obj(BaseThingSerializer.IdKey -> t._id))
          case None => NotFound(Json.obj())
        } recover {
          case e: ClientFormatException => BadRequest(Json.obj(MessageKey -> e.msg))
          case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
          case e: CoherenceException => UnprocessableEntity(Json.obj(MessageKey -> e.msg))
        }
      case e: JsError => Future.successful(BadRequest(BadJsonFormatMessage))
    }
  }

  def getThingsSearch = Action.async(parse.json) { request =>
    request.body.validate[GetThings] match {
      case JsSuccess(getThings, _) =>
        val f = thingUseCase.getThings(getThings)
        seqOfThingsToHttpResponse(f)
      case e: JsError => Future {
        BadRequest(BadJsonFormatMessage)
      }
    }
  }

  def getThings = Action.async { request =>

    val f = thingUseCase.getThings()
    seqOfThingsToHttpResponse(f)

  }

  def getThingsAsStream = Action.async { request =>

    val serializeEnumeratee: Enumeratee[Thing, String] = Enumeratee.map[Thing] { thing =>
      ThingMinifiedSerializer.thingFormat.writes(thing).toString
    }

    val stream = thingUseCase.getThingsAsStream.&>(serializeEnumeratee)

    Future {
      Ok.feed(stream.&>(EventSource())).as("text/event-stream")
    }

/*    Future {
      Ok.chunked(stream)
    }*/

  }

  def getThing(id: String) = Action.async {

    thingUseCase.getThing(id) map {
      case Some(thing) => Ok(ThingSerializer.thingFormat.writes(thing))
      case None => NotFound(Json.obj())
    } recover {
      case e: ClientFormatException => BadRequest(Json.obj(MessageKey -> e.msg))
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }

  }

  def deleteThing(id: String) = Action.async(parse.json) { request =>
    thingUseCase.deleteThing(id) map { id =>
      Ok(Json.obj(BaseThingSerializer.IdKey -> id))
    } recover {
      case e: ClientFormatException => BadRequest(Json.obj(MessageKey -> e.msg))
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }
  }

  def executeAction(id: String, actionName: String) = Action.async(parse.json) { request =>
    thingUseCase.executeThingAction(id, actionName) map {
      case success: ExecutionSuccess => Ok(Json.obj("data" -> success.message))
      case failure: ExecutionFailure => BadRequest(Json.obj("message" -> failure.errors))
    } recover {
      case e: ClientFormatException => BadRequest(Json.obj(MessageKey -> e.msg))
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }
  }

  private def seqOfThingsToHttpResponse(thingsF: Future[Seq[Thing]]): Future[Result] = {
    thingsF map { setOfThings =>
      val json = ThingMinifiedSerializer.thingSeqFormat.writes(setOfThings)
      Ok(json)
    } recover {
      case e: DatabaseException => BadGateway(Json.obj(MessageKey -> e.msg))
    }
  }

}
