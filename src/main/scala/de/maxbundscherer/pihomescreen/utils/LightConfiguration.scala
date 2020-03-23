package de.maxbundscherer.pihomescreen.utils

trait LightConfiguration {

  object Lights {

    abstract class Light(hueLightId: Int)

    case object KitchenTop    extends Light(hueLightId = 7)
    case object KitchenTable  extends Light(hueLightId = 8)
    case object KitchenBottom extends Light(hueLightId = 2)

    case object LivingRoomLeft    extends Light(hueLightId = 5)
    case object LivingRoomTruss   extends Light(hueLightId = 11)
    case object LivingRoomRight   extends Light(hueLightId = 6)
    case object LivingRoomCouch   extends Light(hueLightId = 4)
    case object LivingRoomCloset  extends Light(hueLightId = 10)

    case object BedroomBack   extends Light(hueLightId = 9)
    case object BedroomFront  extends Light(hueLightId = 1)

  }

  object Rooms {

    abstract class Room(lights: Vector[Lights.Light])

    case object Kitchen     extends Room(Vector(Lights.KitchenTop, Lights.KitchenTable, Lights.KitchenBottom))
    case object LivingRoom  extends Room(Vector(Lights.LivingRoomLeft, Lights.LivingRoomTruss, Lights.LivingRoomRight, Lights.LivingRoomCouch, Lights.LivingRoomCloset))
    case object Bedroom     extends Room(Vector(Lights.BedroomBack, Lights.BedroomFront))

  }

}
