package de.maxbundscherer.pihomescreen.utils

trait LightConfiguration {

  object Lights {

    type Light = Int

    val KitchenTop: Light = 7
    val KitchenTable: Light = 8
    val KitchenBottom: Light = 2

    val LivingRoomLeft: Light = 5
    val LivingRoomTruss: Light = 11
    val LivingRoomRight: Light = 6
    val LivingRoomCouch: Light = 4
    val LivingRoomCloset: Light = 10

    val BedroomBack: Light = 9
    val BedroomFront: Light = 1

    val ALL_LIGHTS: Vector[Light] = Vector(KitchenTop, KitchenTable, KitchenBottom, LivingRoomLeft, LivingRoomTruss, LivingRoomRight, LivingRoomCouch, LivingRoomCloset, BedroomBack, BedroomFront)

  }

  object Rooms {

    import Lights._
    
    type Room = Vector[Light]
    
    val Kitchen = Vector(KitchenTop, KitchenTable, KitchenBottom)
    val LivingRoom = Vector(LivingRoomLeft, LivingRoomTruss, LivingRoomRight, LivingRoomCouch, LivingRoomCloset)
    val Bedroom = Vector(BedroomBack, BedroomFront)

    val ALL_ROOMS: Vector[Room] = Vector(Kitchen, LivingRoom, Bedroom)
    
  }

}
