package nz.co.enhance.ServiceClasses;

import java.util.List;

public class PUTRequest extends HTTPRequest {
    /**
     * For a PUT request:
     * - We always have an endpoint
     * - Sometimes that endpoint has parameters we want to set - set them before passing the endpoint in to this request
     * - We always have a body which will upadte the existing record
     * - The body usually has parameters we want to set - do this before passing the body in
     * - Often we have headers but these can be set to null if none are required.
     */

    public PUTRequest(String endpointURL, String body, List<List<String>> headers) {
        this.endpointURL = endpointURL;
        this.body = body;
        this.headers = headers;
        type = "PUT";
        request = new Request(endpointURL, body, headers, type);
    }


}
