/*
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.ws.spi.http;

import javax.xml.ws.handler.MessageContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.Principal;

/**
 * This class encapsulates a HTTP request received and a 
 * response to be generated in one exchange. It provides methods 
 * for examining the request from the client, and for building and 
 * sending the response. 
 * <p>
 * The typical life-cycle of a HttpExchange is shown in the sequence
 * below. 
 * <ol><li>{@link #getRequestMethod()} to determine the command
 * <li>{@link #getRequestHeaders()} to examine the request headers (if needed)
 * <li>{@link #getRequestBody()} returns a {@link ReadableByteChannel} for reading the request body.
 *     After reading the request body, the channel is closed.
 * <li>{@link #getResponseHeaders()} to set any response headers, except content-length
 * <li>{@link #sendResponseHeaders(int,long)} to send the response headers. Must be called before
 * next step.
 * <li>{@link #getResponseBody()} to get a {@link WritableByteChannel} to send the response body.
 *      When the response body has been written, the channel must be closed to terminate the exchange.
 * </ol>
 * <b>Terminating exchanges</b>
 * <br>
 * Exchanges are terminated when both the request Channel and response Channel are closed.
 * Closing the WritableByteChannel, implicitly closes the ReadableByteChannel (if it is not already closed).
 * However, it is recommended
 * to consume all the data from the ReadableByteChannel before closing it.
 * The convenience method {@link #close()} does all of these tasks.
 * Closing an exchange without consuming all of the request body is not an error
 * but may make the underlying TCP connection unusable for following exchanges.
 * The effect of failing to terminate an exchange is undefined, but will typically
 * result in resources failing to be freed/reused.
 *
 * @author Jitendra Kotamraju
 * @since JAX-WS 2.2
 */

public interface HttpExchange {

    /**
     * Standard property: cipher suite value when the request is received over HTTPS
     * <p>Type: String
     */
    public static final String REQUEST_CIPHER_SUITE = "javax.xml.ws.spi.http.request.cipher.suite";

    /**
     * Standard property: bit size of the algorithm when the request is received over HTTPS
     * <p>Type: Integer
     */
    public static final String REQUEST_KEY_SIZE = "javax.xml.ws.spi.http.request.key.size";

    /**
     * Standard property: A SSL certificate, if any, associated with the request
     *
     * <p>Type: java.security.cert.X509Certificate[]
     * The order of this array is defined as being in ascending order of trust.
     * The first certificate in the chain is the one set by the client, the next
     * is the one used to authenticate the first, and so on.
     */
    public static final String REQUEST_X509CERTIFICATE = "javax.xml.ws.spi.http.request.cert.X509Certificate";

    /**
     * Returns an immutable Map containing the HTTP headers that were 
     * included with this request. The keys in this Map will be the header 
     * names, while the values will be a List of Strings containing each value 
     * that was included (either for a header that was listed several times, 
     * or one that accepts a comma-delimited list of values on a single line). 
     * In either of these cases, the values for the header name will be 
     * presented in the order that they were included in the request.
     * <p>
     * The keys in Map are case-insensitive.
     * @return a read-only Map which can be used to access request headers
     */
    Map<String, List<String>> getRequestHeaders();

    /**
     * Returns the value of the specified request header. If the request
     * did not include a header of the specified name, this method returns
     * null. If there are multiple headers with the same name, this method
     * returns the first header in the request. The header name is
     * case-insensitive. You can use this method with any request header.
     *
     * @param name the name of the request header
     * @return returns the value of the requested header,
     *         or null if the request does not have a header of that name
     */
     String getRequestHeader(String name);

    /**
     * Returns a mutable Map into which the HTTP response headers can be stored
     * and which will be transmitted as part of this response. The keys in the 
     * Map will be the header names, while the values must be a List of Strings
     * containing each value that should be included multiple times 
     * (in the order that they should be included).
     * <p>
     * The keys in Map are case-insensitive.
     * @return a writable Map which can be used to set response headers.
     */
    Map<String, List<String>> getResponseHeaders();

    /**
     * Adds a response header with the given name and value. This method allows
     * response headers to have multiple values.
     * 
     * @param name the name of the header
     * @param value the additional header value If it contains octet string,
     *        it should be encoded according to
     *        RFC 2047 (http://www.ietf.org/rfc/rfc2047.txt)
     */
    void addHeader(String name, String value);

    /**
     * Get the request URI
     *
     * @return the request URI 
     */
    URI getRequestURI();

    /**
     * Get the request method
     *
     * @return the request method
     */
    String getRequestMethod();

    /**
     * Get the HttpContext for this exchange
     *
     * @return the HttpContext
     */
    HttpContext getHttpContext();

    /**
     * This must be called to end the exchange.
     *
     * Ends this exchange by doing the following in sequence:<p><ol>
     * <li>close the request ReadableByteChannel, if not already closed<p></li>
     * <li>close the response WritableByteChannel, if not already closed. </li>
     * </ol>
     */
    void close();

    /**
     * returns a Channel from which the request body can be read.
     * Multiple calls to this method will return the same Channel.
     * It is recommended that applications should consume (read) all of the
     * data from this Channel before closing it. If a Channel is closed
     * before all data has been read, then the close() call will 
     * read and discard remaining data (up to an implementation specific
     * number of bytes).
     * @return the stream from which the request body can be read.
     */
    InputStream getRequestBody();

    /**
     * returns a stream to which the response body must be
     * written. {@link #sendResponseHeaders(int,long)}) must be called prior to calling
     * this method. Multiple calls to this method (for the same exchange)
     * will return the same Channel. In order to correctly terminate
     * each exchange, the output Channel must be closed, even if no
     * response body is being sent.
     * <p>
     * If the call to sendResponseHeaders() specified a fixed response
     * body length, then the exact number of bytes specified in that
     * call must be written to this Channel. If too many bytes are written,
     * then write() will throw an IOException. If too few bytes are written
     * then the Channel close() will throw an IOException. In both cases,
     * the exchange is aborted and the underlying TCP connection closed.
     *
     * @return the stream to which the response body is written
     */
    OutputStream getResponseBody();


    /**
     * Starts sending the response back to the client using the current set of response headers
     * and the numeric response code as specified in this method. The response body length is also specified
     * as follows. If the response length parameter is greater than zero, this specifies an exact
     * number of bytes to send and the application must send that exact amount of data. 
     * If the response length parameter is <code>zero</code>, then chunked transfer encoding is
     * used and an arbitrary amount of data may be sent. The application terminates the
     * response body by closing the WritableByteChannel. If response length has the value <code>-1</code>
     * then no response body is being sent.
     * <p>
     * If the content-length response header has not already been set then
     * this is set to the apropriate value depending on the response length parameter.
     * <p>
     * This method must be called prior to calling {@link #getResponseBody()}.
     * @param rCode the response code to send
     * @param responseLength if > 0, specifies a fixed response body length
     * 	      and that exact number of bytes must be written
     *        to the WritableByteChannel acquired from getResponseBody(), or else
     *        if equal to 0, then chunked encoding is used, 
     *        and an arbitrary number of bytes may be written.
     *	      if <= -1, then no response body length is specified and
     *        no response body may be written.
     * @see HttpExchange#getResponseBody()
     * @throws IOException if there is i/o error
     */
    void sendResponseHeaders(int rCode, long responseLength) throws IOException ;

    /**
     * Returns the address of the remote entity invoking this request
     *
     * @return the InetSocketAddress of the caller
     */
    InetSocketAddress getRemoteAddress();

    /**
     * Returns the response code, if it has already been set
     *
     * @return the response code, if available. <code>-1</code> if not available yet.
     */
    int getResponseCode();

    /**
     * Returns the local address on which the request was received
     *
     * @return the InetSocketAddress of the local interface
     */
    InetSocketAddress getLocalAddress();

    /**
     * Returns the protocol string from the request in the form 
     * <i>protocol/majorVersion.minorVersion</i>. For example,
     * "HTTP/1.1"
     *
     * @return the protocol string from the request
     */
    String getProtocol();

    /**
     * Returns an attribute that is associated with
     * HttpExchange. Container may store this object with HttpExchange
     * instances as an out-of-band communication mechanism. JAX-WS handlers
     * and endpoints may then access this object via {@link MessageContext}.
     * <p>
     * Servlet containers must expose {@link MessageContext#SERVLET_CONTEXT},
     * {@link MessageContext#SERVLET_REQUEST}, {@link MessageContext#SERVLET_RESPONSE}
     * as attributes.
     *
     * <p>If the request has been received by the container using HTTPS, the
     * following information must be exposed as attributes. These attributes
     * are {@link #REQUEST_CIPHER_SUITE}, {@link #REQUEST_KEY_SIZE}. If there
     * is a SSL certificate associated with the request, it must be exposed
     * using {@link #REQUEST_X509CERTIFICATE}
     *
     * @param name attribute name
     * @return the attribute value, or null if they do not exist
     */
    Object getAttribute(String name);

    /**
     * Gives all the attribute names that are associated with
     * HttpExchange.
     *
     * @return Iterator for all attribute names
     * @see #getAttribute(String)
     */
    Iterator<String> getAttributeNames();


    /**
     * Returns the {@link Principal} that represents the authenticated
     * user for this HttpExchange.
     *
     * @return Principal for an authenticated user
     *         null otherwise
     */
    Principal getUserPrincipal();

    /**
     * Returns a boolean indicating whether the authenticated user is
     * included in the specified logical "role".
     *
     * @param role specifies the name of the role
     * @return true if the user making this request belongs to a given role
     *         false if the user has not been authenticated
     */
    boolean isUserInRole(String role);


    /**
     * Returns the name of the scheme used to make this request,
     * for example, http, or https.
     *
     * @return name of the scheme used to make this request
     */
    String getScheme();

}