/* --------------------------------------------------------------------------------------------------------
 * DATE:	16 Apr 2015
 * AUTHOR:	Cloete A.H
 * PROJECT:	M-Eng, Inteligent geyser M2M system.	
 * ---------------------------------------------------------------------------------------------------------
 * DESCRIPTION: Uses org.eclipse.om2m.commons.util.XmlMapper and org.eclipse.om2m.commons.resources to 
 * 				create required XML strings for OM2M platform
 * ---------------------------------------------------------------------------------------------------------
 * PURPOSE: 
 * ---------------------------------------------------------------------------------------------------------
 */

package acza.sun.ee.geyserM2M.controller;

import org.eclipse.om2m.commons.resource.APoCPath;
import org.eclipse.om2m.commons.resource.APoCPaths;
import org.eclipse.om2m.commons.resource.Application;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.utils.XmlMapper;

public class M2MxmlFactory {

	public static String registerApplication(String appID){
		Application app = new Application();
		app.setAppId(appID);
		
		return XmlMapper.getInstance().objectToXml(app);
	}
	
	//URI localhost:8181/om2m/gscl/applications
	public static String registerApplication(String appID, String apoc){
		Application app = new Application();
		APoCPaths apocpaths = new APoCPaths();
		APoCPath apocpath = new APoCPath();
		
		apocpath.setPath(apoc);
		apocpaths.getAPoCPath().add(apocpath);
		app.setAPoCPaths(apocpaths);
		app.setAppId(appID);
		
		return XmlMapper.getInstance().objectToXml(app);
	}
	
		
	public static String addContainer(String containerID, Long size){
		Container container = new Container(containerID);
		container.setMaxNrOfInstances(size);
		/*
		 * For some reason, if you set this too 1, NO content instances show up!!
		 * This is actually gives stranger behaviour:
		 	* In the tree it shows up as size-1.
		 	* But in the info box it still gives a total count.
		 	* (Get to the bottom of this soon)
		 */
		
		
		return XmlMapper.getInstance().objectToXml(container);
	}
}

