package de.maxbundscherer.pihomescreen.services.abstracts

abstract class IssLocationService {

  def isIssOverMyHouse: Either[String, Boolean]

}
