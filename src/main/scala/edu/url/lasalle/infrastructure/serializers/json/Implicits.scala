package edu.url.lasalle.infrastructure.serializers.json

import edu.url.lasalle.infrastructure.serializers.json.dto.{CreateThingSerializer, GetThingsSerializer}

object Implicits {

  implicit val thingJsonSerializer = ThingSerializer.ThingWrites

  implicit val actionJsonSerializer = ActionSerializer.actionFormat

  implicit val createThingSerializer = CreateThingSerializer.createThingReads

  implicit val getThingsSerializer = GetThingsSerializer.getThingsReads
}
