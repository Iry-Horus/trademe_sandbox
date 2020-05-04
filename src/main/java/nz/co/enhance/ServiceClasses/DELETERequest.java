package nz.co.enhance.ServiceClasses;

import java.util.List;

public class DELETERequest extends HTTPRequest {
    /**
     * For a DELETE request:
     * - We always have an endpoint
     * - Sometimes that endpoint has parameters we want to set - set them before passing the endpoint in to this request
     * - We don't have a body
     * - Often we have headers but these can be set to null if none are required.
     */

    public DELETERequest(String endpointURL, List<List<String>> headers) {
        this.endpointURL = endpointURL;
        this.headers = headers;
        type = "DELETE";
        request = new Request(endpointURL, null, headers, type);
    }
}
