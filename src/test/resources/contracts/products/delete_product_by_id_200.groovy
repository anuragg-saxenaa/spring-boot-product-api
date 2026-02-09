package contracts.products

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description 'should delete an existing product'
    request {
        method DELETE()
        urlPath $(consumer(regex('/api/products/[0-9]+')), producer('/api/products/1'))
    }
    response {
        status OK()
    }
}
