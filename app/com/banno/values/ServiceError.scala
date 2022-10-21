package com.banno.values

case class ServiceError(errorMessage: String, httpStatusCode: Option[Int] = None) {
  override def toString: String = {
    s"$errorMessage${httpStatusCode.map(code => " - Status Code: " + code).getOrElse("")}"
  }
}
