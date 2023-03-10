/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generator;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.squareup.javapoet.CodeBlock;

import dto.InterfaceMetadata;
import dto.OperationInt;

/**
 *
 * Generation Utilities
 *
 * @author cripan
 * 
 */
public class GenerationUtils {

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	/**
	 * Creates a Jackson object mapper based on a media type. It supports JSON, JSON
	 * Smile, XML, YAML and CSV.
	 * 
	 * @param MediaType  The media type of the encoding
	 * @param MapperName ???
	 * @return The Jackson object mapper.
	 */
	public static CodeBlock createObjectMapper(String MediaType, String MapperName) {

		CodeBlock.Builder BmapperBlock = CodeBlock.builder();

		String mapperCode = ""; // TODO Not Used

		if (MediaType.equalsIgnoreCase("JSON"))
			BmapperBlock.addStatement("$T jsonFactory_$L = new JsonFactory()", JsonFactory.class, MapperName)
					.addStatement("$T $L=new ObjectMapper(jsonFactory_$L)", ObjectMapper.class, MapperName, MapperName);

		else if (MediaType.equalsIgnoreCase("JSON_SMILE"))
			BmapperBlock.addStatement("$T smileFactory = new SmileFactory()", SmileFactory.class)
					.addStatement("ObjectMapper $L=new ObjectMapper(smileFactory)", ObjectMapper.class, MapperName);

		else if (MediaType.equalsIgnoreCase("CBOR"))
			BmapperBlock.addStatement("$T cborFactory = new CBORFactory()", CBORFactory.class)
					.addStatement("$T $L=new ObjectMapper(cborFactory)", ObjectMapper.class, MapperName);

		else if (MediaType.equalsIgnoreCase("XML")) // TODO Check if XMLFactory is needed
			BmapperBlock.addStatement("$T $L=new $T()", ObjectMapper.class, MapperName, XmlMapper.class);
			
			// javax.xml.stream.XMLInputFactory xif = XmlFactoryProvider.newInputFactory();
			// xif.setProperty(javax.xml.stream.XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
			// isExpandingEntityRefs());
			// xif.setProperty(javax.xml.stream.XMLInputFactory.SUPPORT_DTD,
			// isExpandingEntityRefs());
			// xif.setProperty(javax.xml.stream.XMLInputFactory.IS_VALIDATING,
			// isValidatingDtd());
			// javax.xml.stream.XMLOutputFactory xof =
			// XmlFactoryProvider.newOutputFactory();
			// XmlFactory xmlFactory = new XmlFactory(xif, xof);
			// xmlFactory.configure(Feature.AUTO_CLOSE_TARGET, false);
			// result = new XmlMapper(xmlFactory);

		else if (MediaType.equalsIgnoreCase("CSV"))
			BmapperBlock.addStatement("$T csvFactory = new CsvFactory()", CsvFactory.class)
					.addStatement("$T $L=new $T(csvFactory))", ObjectMapper.class, CsvMapper.class, MapperName);

		else
			BmapperBlock.addStatement("$T $L=new ObjectMapper()", ObjectMapper.class, MapperName);

		return BmapperBlock.build();
	}

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Checks that at least one service interface implements the CoAP protocol
	 * 
	 * @param serviceInterfaces List of the service interfaces registered for a system
	 * @return If this system implements CoAP
	 */
	public static boolean checkCoapProtocol(ArrayList<InterfaceMetadata> serviceInterfaces) {
		
		for (int i = 0; i < serviceInterfaces.size(); i++)
			if (serviceInterfaces.get(i).getProtocol().equalsIgnoreCase("CoAP"))
				return true;

		return false;
	}

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Checks that at least one service interface implements the HTTP protocol
	 * 
	 * @param serviceInterfaces List of the service interfaces registered for a system
	 * @return If this system implements HTTP
	 */
	public static boolean checkHttpProtocol(ArrayList<InterfaceMetadata> serviceInterfaces) {
		
		for (int i = 0; i < serviceInterfaces.size(); i++)
			if (serviceInterfaces.get(i).getProtocol().startsWith("HTTP"))
				return true;

		return false;
	}
	
	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Build payload class
	 * 
	 * @param Directory The path to the file
	 * @param name The name of the class
	 * @param system The name of the system
	 * @param op The operation of the interface
	 * @param systemType The type of the system (consumer/provider)
	 */
	public static void objectClassGen(String Directory, String name, String system, OperationInt op, String systemType) {

		if (op.isRequest()) { // If it is a request operation
			ClassSimple Request = new ClassSimple();

			for (int k = 0; k < op.getElements_request().size(); k++) { // Add request payload
				ArrayList<String[]> elements_request = op.getElements_request().get(k).getElements();
				if (systemType.equals("consumer"))
					ConsumerMain.classesRequest.add(Request.classGen(elements_request, op.getOpName() + "RequestDTO", Directory, name, system + "_Consumer"));
				else if (systemType.equals("provider"))
					ProviderMain.classesRequest.add(Request.classGen(elements_request, op.getOpName() + "RequestDTO", Directory, name, system + "_Provider"));
			}
		}

		if (op.isResponse()) { // If it is a response operation
			ClassSimple Response = new ClassSimple();

			for (int j = 0; j < op.getElements_response().size(); j++) { // Add response payload
				ArrayList<String[]> elements_response = op.getElements_response().get(j).getElements();
				if (systemType.equals("consumer"))
					ConsumerMain.classesResponse = Response.classGen(elements_response, op.getOpName() + "ResponseDTO", Directory, name, system + "_Consumer");
				else if(systemType.equals("provider"))
					ProviderMain.classesRequest.add(Response.classGen(elements_response, op.getOpName() + "RequestDTO", Directory, name, system + "_Provider"));
			}

		}
	}
		
	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Removes the service interface repetitions
	 * 
	 * @param serviceInterfaces List of the service interfaces registered for a system
	 * @return Set of service interfaces
	 */
	public static ArrayList<InterfaceMetadata> removeRepetitions(ArrayList<InterfaceMetadata> serviceInterfaces) {
		// TODO: Look if this is correct or the problem is the service name convention
		
		for (int i = 0; i < serviceInterfaces.size(); i++) {
			InterfaceMetadata inter = serviceInterfaces.get(i);
			
			for (int j = i + 1; j < serviceInterfaces.size(); j++) {
				InterfaceMetadata interNext = serviceInterfaces.get(j);
				if (inter.getID().equals(interNext.getID()))
					serviceInterfaces.remove(j);
			}
		}
		return serviceInterfaces;
	}
}
