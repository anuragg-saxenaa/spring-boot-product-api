package contracts.products

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description 'should create a new product'
    request {
        method POST()
        url '/api/products'
        headers {
            contentType(applicationJson())
        }
        body(
            name: $(consumer(anyNonBlankString()), producer('Contract Created Product')),
            description: $(consumer(optional(anyNonBlankString())), producer('Created via contract')),
            price: $(consumer(anyDouble()), producer(123.45)),
            category: $(consumer(anyNonBlankString()), producer('Contract')),
            stockQuantity: $(consumer(anyInteger()), producer(5))
            // sku intentionally omitted (server may generate non-deterministically)
        )
    }
    response {
        status CREATED()
        headers {
            contentType(applicationJson())
        }
        bodyMatchers {
            // verify minimum stable fields; ignore timestamps / generated sku
            jsonPath('$.id', byType())
            jsonPath('$.name', byType())
            jsonPath('$.price', byType())
            jsonPath('$.category', byType())
            jsonPath('$.stockQuantity', byType())
        }
    }
}
