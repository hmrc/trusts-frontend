package controllers

class $className;format="cap"$Controller @Inject()(
                                         override val messagesApi: MessagesApi,
                                         registrationsRepository: registrationsRepository,
                                         navigator: Navigator,
                                         identify: IdentifyForRegistration,
                                         getData: DraftIdRetrievalActionProvider,
                                         requireData: DataRequiredAction,
                                         formProvider: $className$FormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         view: $className$View
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {


  val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = (identify andThen getData(draftId) andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get($className$Page) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId))
  }

  def onSubmit(mode: Mode, draftId : String) = (identify andThen getData(draftId) andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set($className$Page, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage($className$Page, mode, draftId)(updatedAnswers))
        }
      )
  }
}
