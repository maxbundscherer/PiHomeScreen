package de.maxbundscherer.pihomescreen.utils

trait LightConfiguration {

  object Lights {

    type Light = Int

    val KitchenTop: Light    = 7
    val KitchenTable: Light  = 8
    val KitchenBottom: Light = 2

    val LivingRoomLeft: Light   = 5
    val LivingRoomTruss: Light  = 11
    val LivingRoomRight: Light  = 6
    val LivingRoomCouch: Light  = 4
    val LivingRoomCloset: Light = 10

    val BedroomBack: Light  = 12
    val BedroomFront: Light = 1

    val ALL_LIGHTS: Vector[Light] = Vector(
      KitchenTop,
      KitchenTable,
      KitchenBottom,
      LivingRoomLeft,
      LivingRoomTruss,
      LivingRoomRight,
      LivingRoomCouch,
      LivingRoomCloset,
      BedroomBack,
      BedroomFront
    )

  }

  object Rooms {

    import Lights._

    type Room    = Vector[Light]
    type GroupId = Int

    val Kitchen: Room = Vector(KitchenTop, KitchenTable, KitchenBottom)
    val LivingRoom: Room =
      Vector(LivingRoomLeft, LivingRoomTruss, LivingRoomRight, LivingRoomCouch, LivingRoomCloset)
    val Bedroom: Room = Vector(BedroomBack, BedroomFront)

    val KitchenGroupId: GroupId    = 2
    val LivingRoomGroupId: GroupId = 1
    val BedroomGroupId: GroupId    = 3

    val ALL_ROOMS: Vector[Room] = Vector(Kitchen, LivingRoom, Bedroom)

  }

  object Scenes {

    import Rooms._

    /**
      * Tuple (hueGroupId, hueSceneName)
      */
    type Scene = (GroupId, String)

    val KitchenRead: Scene  = (KitchenGroupId, "L4A2frS39HBYt3b")
    val KitchenRelax: Scene = (KitchenGroupId, "TSdxxGkxdTPpEYU")

    val LivingRoomRead: Scene   = (LivingRoomGroupId, "fezQWB7oMIW0Viu")
    val LivingRoomRelax: Scene  = (LivingRoomGroupId, "2sy15kwZAhWZsAS")
    val LivingRoomDimmed: Scene = (LivingRoomGroupId, "KU-raZZ4nh9BxNM")
    val LivingRoomSky: Scene    = (LivingRoomGroupId, "-TsjoV5iDYMxo33")
    val LivingRoomSleep: Scene  = (LivingRoomGroupId, "vNwLwB2XeTVTZfn")

    val BedroomRead: Scene       = (BedroomGroupId, "hzovBAwEPoiMO9P")
    val BedroomNightLight: Scene = (BedroomGroupId, "pxX-dtLRGZ-lBTP")
    val BedroomRelax: Scene      = (BedroomGroupId, "jy0fpjxdpEhzKHz")
    val BedroomRed: Scene        = (BedroomGroupId, "0tVU-NjBn0BIZ4d")
    val BedroomSleep: Scene      = (BedroomGroupId, "4yLVlXazhSV1xqw")

  }

  object Routines {

    /**
      * Tuple (Vector of scenes that should be activated, Vector of rooms that should be turned off)
      */
    type Routine = (Vector[Scenes.Scene], Vector[Rooms.Room])

    val WakeUp: Routine = (Vector(Scenes.LivingRoomRead, Scenes.KitchenRead), Vector(Rooms.Bedroom))
    val Relax: Routine =
      (Vector(Scenes.LivingRoomRelax, Scenes.KitchenRelax), Vector(Rooms.Bedroom))
    val DarkRed: Routine = (Vector(Scenes.LivingRoomSky), Vector(Rooms.Bedroom, Rooms.Kitchen))
    val Sleep: Routine =
      (Vector(Scenes.LivingRoomSleep, Scenes.BedroomSleep), Vector(Rooms.Kitchen))
    val AllOff: Routine = (Vector(), Rooms.ALL_ROOMS)

  }

}
