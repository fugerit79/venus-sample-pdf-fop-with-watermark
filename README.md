# venus-sample-pdf-fop-with-watermark

This is a sample project configured using [fj-doc-maven-plugin init plugin](https://venusdocs.fugerit.org/guide/#maven-plugin-goal-init).

[![Keep a Changelog v1.1.0 badge](https://img.shields.io/badge/changelog-Keep%20a%20Changelog%20v1.1.0-%23E05735)](CHANGELOG.md)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fugerit79_venus-sample-pdf-fop-with-watermark&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=fugerit79_venus-sample-pdf-fop-with-watermark)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=fugerit79_venus-sample-pdf-fop-with-watermark&metric=coverage)](https://sonarcloud.io/summary/new_code?id=fugerit79_venus-sample-pdf-fop-with-watermark)
[![License: MIT](https://img.shields.io/badge/License-MIT-teal.svg)](https://opensource.org/licenses/MIT)
[![code of conduct](https://img.shields.io/badge/conduct-Contributor%20Covenant-purple.svg)](https://github.com/fugerit-org/fj-universe/blob/main/CODE_OF_CONDUCT.md)

## Requirement

* JDK 8+ (*)
* Maven 3.8+

(*) Currently FOP not working on [JDK 25, See bug JDK-8368356](https://bugs.openjdk.org/browse/JDK-8368356).

## Project initialization

This project was created with [Venus Maven plugin](https://venusdocs.fugerit.org/guide/#maven-plugin-goal-init)

```shell
mvn org.fugerit.java:fj-doc-maven-plugin:8.17.0:init \
-DgroupId=org.fugerit.java.demo \
-DartifactId=venus-sample-pdf-fop-with-watermark \
-Dextensions=base,freemarker,mod-fop \
-DwithCI=GitHub
```

### Watermark from template

This project shows how to create a watermark from template.

```java
    @Test
    void testDocProcessTemplateWatermark() throws Exception {
        File output = this.testWorker( "template" );
        Assertions.assertNotEquals( 0, output.length() );
    }
```

Adding the attribute to data model : 

```java
    DocProcessContext context = DocProcessContext.newContext("listPeople", listPeople)
        .withAtt(ATT_WATERMARK_MODE, watermarkMode );
```

And enabling the ftl template : 

```ftl
        <#if (watermarkMode!'') == 'template'>
        <header-ext>
            <image url="jpg" scaling="300" align="center" alt="Watermark" base64="${imageBase64CLFun('venus-sample-pdf-fop-with-watermark/img/watermark.jpg')}" />
        </header-ext>
        </#if>
```

NOTE: you will need to create [watermark image](src/main/resources/venus-sample-pdf-fop-with-watermark/img/watermark.jpg).

### Watermark from code

It is possible to add watermark text programmatically. In this example we are going to use [Apache PDFBox](https://pdfbox.apache.org/) : 

```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.3</version>
    <scope>test</scope>
</dependency>
```

And the code : 

```java
 private static byte[] addTextWatermark( byte[] input, String watermarkText) throws IOException {
    try ( PDDocument document = Loader.loadPDF( input );
          ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        // Iterate through all pages
        for (PDPage page : document.getPages()) {
            PDPageContentStream contentStream = new PDPageContentStream(
                    document, page, PDPageContentStream.AppendMode.APPEND, true, true);

            // Set transparency
            PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
            graphicsState.setNonStrokingAlphaConstant(0.3f); // 30% opacity
            contentStream.setGraphicsStateParameters(graphicsState);

            // Set font and color
            contentStream.setFont(font, 120);
            contentStream.setNonStrokingColor( 0.2F, 0.2F, 0.2F); // Light gray

            // Calculate position (center of page, diagonal)
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();

            // Position and rotate the text
            contentStream.beginText();
            contentStream.setTextMatrix(
                    Matrix.getRotateInstance(Math.toRadians(45),
                            pageWidth / 5,
                            pageHeight / 5));
            contentStream.showText(watermarkText);
            contentStream.endText();

            contentStream.close();
        }
        document.save(buffer);
        return buffer.toByteArray();
    }
}
```