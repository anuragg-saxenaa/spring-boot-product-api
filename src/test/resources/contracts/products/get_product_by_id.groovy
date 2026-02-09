package contracts.products

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description 'should return a product by id'
    request {
        method GET()
        urlPath $(consumer(regex('/api/products/[0-9]+')), producer('/api/products/1'))
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        bodyMatchers {
            jsonPath('$.id', byType())
            jsonPath('$.name', byType())
            jsonPath('$.price', byType())
        }
    }
}
