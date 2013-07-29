import app.FoaasModule
import app.FuckOffService
import org.ratpackframework.groovy.templating.TemplateRenderer

import static groovy.json.JsonOutput.toJson
import static org.ratpackframework.groovy.RatpackScript.ratpack

ratpack {
    modules {
        register new FoaasModule(getClass().getResource("messages.properties"))
    }

    handlers {
        get(":type/:p1/:p2?") { TemplateRenderer renderer, FuckOffService service ->
            def to = pathTokens.p2 ? pathTokens.p1 : null
            def from = pathTokens.p2 ?: pathTokens.p1

            def f = service.get(pathTokens.type, from, to)
            if (f.values().every { it == null }) {
                clientError 404
            }

            accepts.type("text/plain") {
                response.send "$f.message $f.subtitle"
            }.type("text/html") {
                renderer.render f, "fuckoff.html"
            }.type("application/json") {
                response.send toJson(f)
            }.send()
        }

        assets "public", "index.html"
    }
}