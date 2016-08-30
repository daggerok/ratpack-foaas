package app

import groovy.json.JsonSlurper

class ApiSpec extends FunctionalSpec {

  def "content negotiation"() {
    when:
    requestSpec {
      it.headers.add("Accept", "*/*")
    }

    then:
    getText("off/to/from") == "Fuck off, to. - from"
  }

  def "Content Negotiation JSON"() {
    when:
    requestSpec {
      it.headers.add("Accept", "application/json")
    }

    then:
    def responseJson = new JsonSlurper().parseText(get("off/to/from").body.text)
    with(responseJson) {
      message == "Fuck off, to."
      subtitle == "- from"
    }
  }

  def "Content Negotiation text/HTML"() {
    when:
    requestSpec {
      it.headers.add("Accept", "text/html")
    }

    then:
    with(get("off/to/from")) {
      headers["content-type"] == "text/html;charset=UTF-8"
      body.text.contains "Fuck off, to."
    }
  }

  def "Content Negotiation pdf"() {
    when:
    requestSpec {
      it.headers.add("Accept", "application/pdf")
    }

    then:
    with(get("off/to/from")) {
      headers["content-type"] == "application/pdf"
      body.text.startsWith "%PDF"
    }
  }

  def "handles unknowns"() {
    when:
    get("foo/bar/baasdfa")

    then:
    response.statusCode == 404
  }

  def "pathtokens are properly encoded"() {
    when:
    requestSpec {
      it.headers.add("Accept", "text/html")
    }

    then:
    with(get("blink182/generation/fucking%20heart")) {
      !body.text.contains("%20")
    }
  }

  def "variant text is produced by #path"() {
    when:
    requestSpec {
      it.headers.add("Accept", "*/*")
    }

    then:
    getText(path) == expectedText

    where:
    path                      | expectedText
    "keepcalm/to/from"        | "Keep Calm to and Fuck Off - from"
    "steviewonder/to/from"    | "I just called, to say..........to FUCK OFF! - from"
    "malcolmtucker/from"      | "I will tear your fucking skin off, I will wear it to your mother's birthday party and rub your nuts up and down her leg whilst whistling Bohemian fucking Rhapsody. Right? - from"
    "justinbieber/from"       | "There's gonna be times in your life when people say you can't do something. And there's gonna be times in your life when people say that you can't live your dreams. This is what I tell them: FUCK OFF! - from"
    //"swearengen/E.B./Al"      | "I will profane your fucking remains, E.B.! - Al"
    "arthur/to/from"          | "How shall to fuck off, O Lord? - from"
    "ceccoangiolieri/to/from" | "to, s'i' so' buon begolardo, - tu me ne tien' ben la lancia a le reni; - s'i' desno con altrui, e tu vi ceni; - s'io mordo 'l grasso, e tu vi sughi el lardo; - s'io cimo 'l panno, e tu vi freghi el cardo. - from"
    "boehner/from"            | "While thousands of programs and employees were deemed \"non-essential\" and suspended or furloughed, one of the few government costs deemed essential to maintain during the  shutdown has been the Congressional gym. Oh no, please, don't get out of the pool, Mr. Boehner. This fuck you is waterproof. - from"
    "british/to/from"         | "Thanks a bunch, to! - from"
  }
}
