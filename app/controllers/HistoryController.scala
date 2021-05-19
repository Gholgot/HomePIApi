package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents, Action, AnyContent}
import utils.Auth

@Singleton
class HistoryController(
  cc: ControllerComponents,
  authUtils: Auth
  ) extends AbstractController(cc) {

  def get(): Action[AnyContent] = authUtils(parse.defaultBodyParser) { req =>
    Ok("")
  }
}