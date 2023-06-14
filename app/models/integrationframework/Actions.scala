package models.integrationframework

import play.api.libs.json.{Format, Json}

case class Actions(actionsTaxCalc: Seq[ActionDetails]
                  )

object Actions {

  implicit val format: Format[Actions] = Json.format[Actions]

}
