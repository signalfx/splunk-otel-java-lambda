package com.splunk.support.lambda.examples;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

public class RequestStreamFunction implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {

        String input = IOUtils.toString(inputStream);
        System.out.println(input);
        IOUtils.write("Hi "+input, outputStream);
        outputStream.close();
    }
}
