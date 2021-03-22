package com.myorg;

import java.util.HashMap;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigateway.DomainNameOptions;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.apigateway.SecurityPolicy;
import software.amazon.awscdk.services.certificatemanager.Certificate;
import software.amazon.awscdk.services.certificatemanager.ICertificate;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.s3.Bucket;

public class AwsCdk13512Stack extends Stack {
    public AwsCdk13512Stack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsCdk13512Stack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

		Bucket bucket = new Bucket(this, "WidgetStore");

		Function handler = Function.Builder.create(this, "WidgetHandler").runtime(Runtime.NODEJS_14_X)
				.code(Code.fromAsset("resources")).handler("widgets.main").environment(new HashMap<String, String>() {
					{
						put("BUCKET", bucket.getBucketName());
					}
				}).build();

		bucket.grantReadWrite(handler);
		
        String domainName = "api.domain.com";
        SecurityPolicy securityPolicy = SecurityPolicy.TLS_1_2;
        String certificateArn = "<removedARN>";
        ICertificate certificate = Certificate.fromCertificateArn(this, "sslCertificate", certificateArn);
        RestApi api = RestApi.Builder.create(this, "Widgets-API").restApiName("Widget Service")
        .description("This service services widgets.").domainName(DomainNameOptions.builder()
        .domainName(domainName).securityPolicy(securityPolicy).certificate(certificate).build())
        .build();
        
		// Add new widget to bucket with: POST /{id}
		LambdaIntegration postWidgetIntegration = new LambdaIntegration(handler);

		// Get a specific widget from bucket with: GET /{id}
		LambdaIntegration getWidgetIntegration = new LambdaIntegration(handler);

		// Remove a specific widget from the bucket with: DELETE /{id}
		LambdaIntegration deleteWidgetIntegration = new LambdaIntegration(handler);

		Resource widget = api.getRoot().addResource("{id}");

		widget.addMethod("POST", postWidgetIntegration); // POST /{id}
		widget.addMethod("GET", getWidgetIntegration); // GET /{id}
		widget.addMethod("DELETE", deleteWidgetIntegration); // DELETE /{id}
    }
}
