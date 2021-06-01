/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arrowhead.skelettons.deployment.generator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import  com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.squareup.javapoet.CodeBlock;
/**
 *
 * @author cripan
 */
public class EncodingParser {

   
    
    
    /**
     * Creates a Jackson object mapper based on a media type. It supports JSON,
     * JSON Smile, XML, YAML and CSV.
     * 
     * @return The Jackson object mapper.
     */
 public static CodeBlock createObjectMapper(String MediaType, String MapperName) {
     
      CodeBlock.Builder BmapperBlock = CodeBlock.builder();
           
    String mapperCode="";
    
    if (MediaType.equalsIgnoreCase("JSON")) {
       BmapperBlock
               .addStatement("$T jsonFactory_$L = new JsonFactory()",JsonFactory.class,MapperName)
               .addStatement("$T $L=new ObjectMapper(jsonFactory_$L)",ObjectMapper.class,MapperName, MapperName);
                
    } else if (MediaType.equalsIgnoreCase("JSON_SMILE")) {
         BmapperBlock
                  .addStatement("$T smileFactory = new SmileFactory()",SmileFactory.class)
                  .addStatement("ObjectMapper $L=new ObjectMapper(smileFactory)",ObjectMapper.class,MapperName);
        
    } else if (MediaType.equalsIgnoreCase("CBOR")) {
         BmapperBlock
                .addStatement( "$T cborFactory = new CBORFactory()",CBORFactory.class)
               .addStatement("$T $L=new ObjectMapper(cborFactory)",ObjectMapper.class,MapperName);
        

    } else if (MediaType.equalsIgnoreCase("XML")) { //TO DO: CHECK IF I NEED THE XMLFACTORY OR NOT
       //javax.xml.stream.XMLInputFactory xif = XmlFactoryProvider.newInputFactory();
        //xif.setProperty(javax.xml.stream.XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, isExpandingEntityRefs());
        //xif.setProperty(javax.xml.stream.XMLInputFactory.SUPPORT_DTD, isExpandingEntityRefs());
        //xif.setProperty(javax.xml.stream.XMLInputFactory.IS_VALIDATING, isValidatingDtd());
        //javax.xml.stream.XMLOutputFactory xof = XmlFactoryProvider.newOutputFactory();
        //XmlFactory xmlFactory = new XmlFactory(xif, xof);
       // xmlFactory.configure(Feature.AUTO_CLOSE_TARGET, false);
        //result = new XmlMapper(xmlFactory);
        
         BmapperBlock.addStatement( "$T $L=new $T()",ObjectMapper.class,MapperName, XmlMapper.class);

        
    } else if (MediaType.equalsIgnoreCase("CSV")) {
        BmapperBlock
                .addStatement("$T csvFactory = new CsvFactory()",CsvFactory.class)
                .addStatement( "$T $L=new $T(csvFactory))",ObjectMapper.class,CsvMapper.class,MapperName);
        
  
    } else {
         BmapperBlock.addStatement("$T $L=new ObjectMapper()",ObjectMapper.class,MapperName);
    }
    
    
    return  BmapperBlock.build();
}
    
}
