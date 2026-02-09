package contracts.products

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description 'should return 404 when product does not exist'
    request {
        method GET()
        urlPath $(consumer(regex('/api/products/[0-9]+')), producer('/api/products/999999'))
    }
    response {
        status NOT_FOUND()
    }
}
