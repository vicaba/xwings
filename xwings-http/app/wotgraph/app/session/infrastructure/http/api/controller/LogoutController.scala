package wotgraph.app.session.infrastructure.http.api.controller

import play.api.mvc.{Action, Controller}


class LogoutController extends Controller {

  def execute = Action { implicit r =>
    Ok("").withNewSession
  }

}
