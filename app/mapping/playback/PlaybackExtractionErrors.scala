package mapping.playback

object PlaybackExtractionErrors {

  sealed trait PlaybackExtractionError

  case object FailedToExtractData extends PlaybackExtractionError
  case object FailedToCombineAnswers extends RuntimeException with PlaybackExtractionError

}
