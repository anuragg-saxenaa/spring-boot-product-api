package contracts.products

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description 'should return all products'
    request {
        method GET()
        url '/api/products'
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body([
            [
                id: $(anyNumber()),
                name: $(anyNonBlankString()),
                price: $(anyDouble())
            ]
        ])
        bodyMatchers {
            jsonPath('$', byType { minOccurrence(1) })
        }
    }
}
