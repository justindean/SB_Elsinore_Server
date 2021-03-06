package com.sb.common;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class ServePID {
	// This class is used to serve the webpage for the PID control
	// Also known as the Elsinore web page
	// I can easily refactor this for a better name later
	public String lineSep = System.getProperty("line.separator");
	private HashMap<String, String> devices;
	public ServePID(HashMap<String, String> devList) {
		// no passed values, just generate the basic data
		
		devices = devList;
	}
	
	
	public String getHeader() {
		String header = "";
		return header;
	}
	
	public String getPage() {
		String page = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'><meta content='text/html; charset=UTF-8' http-equiv='Content-Type'/></head>" + lineSep;
		page = page + "<title>PID Dislay page</title>" + lineSep +
				"<body>" + lineSep +
				getHeader() + lineSep +
				addJS() + lineSep;

		Iterator devIterator = devices.entrySet().iterator();
		
		while ( devIterator.hasNext()) {
			Map.Entry pairs = (Map.Entry) devIterator.next();
			String devName = (String) pairs.getKey();
			String devType = (String) pairs.getValue();
			if (devType.equalsIgnoreCase("PID")) {
				page += addController(devName, devType);
			}
		}
		// iterate the Temp devices to make them into a table
		devIterator = devices.entrySet().iterator();
		page += lineSep + " <div id='tempProbes'>" + lineSep;
		while ( devIterator.hasNext()) {
			Map.Entry pairs = (Map.Entry) devIterator.next();
			String devName = (String) pairs.getKey();
			String devType = (String) pairs.getValue();
			if (!devType.equalsIgnoreCase("PID")) {
				page += addController(devName, devType);
			}
		}
		page += lineSep + " </div>" + lineSep;

		page += "</body>";
		return page;
	}
	
	public String addJS() {
		String javascript =  "<link rel=\"stylesheet\" type=\"text/css\" href=\"/templates/static/raspibrew.css\" />" 
					+ lineSep +
	        "<!--[if IE]><script type=\"text/javascript\" src=\"excanvas.js\"></script><![endif]-->" + lineSep +
	        
	        "<script type=\"text/javascript\" src=\"/templates/static/jquery.js\"></script>" + lineSep +   
		
	        "<script type=\"text/javascript\" src=\"/templates/static/jquery.fs.stepper.js\"></script>" + lineSep +

	        
	        "<script type=\"text/javascript\" src=\"/templates/static/segment-display.js\"></script>" + lineSep +
	        "<script type=\"text/javascript\" src=\"/templates/static/pidFunctions.js\"></script>" + lineSep +
		"<script type=\"text/javascript\" src=\"/templates/static/raphael.2.1.0.min.js\"></script>" + lineSep +
		"<script type=\"text/javascript\" src=\"/templates/static/justgage.js\"></script>" + lineSep +
	        "<script type=\"text/javascript\">" + lineSep +
	        "var update = 1;" + lineSep +
	        "var GaugeDisplay = {}; " + lineSep +
	        "var Gauges = {}; " + lineSep;

		javascript +=  "//long polling - wait for message" + lineSep +
	         
	        "jQuery(document).ready(function() {" + lineSep +
	        "	waitForMsg();" + lineSep +
        	"});" + lineSep +
	        "</script>";
		
		return javascript;

	}
	
	public String addController(String device, String type) {
		String controller = "<div id=\"" + device + "\" class=\"holo-content controller " + type +"\">" + lineSep +
					"<script type='text/javascript'>" + lineSep +
					"jQuery(document).ready(function() {" + lineSep +

					"GaugeDisplay[\"" + device + "\"] = new SegmentDisplay(\""+device +"-tempGauge\");" + lineSep +
					"GaugeDisplay[\"" + device + "\"].pattern         = \"###.##\";" + lineSep +
					"GaugeDisplay[\"" + device + "\"].angle    = 0;" + lineSep +
					"GaugeDisplay[\"" + device + "\"].digitHeight     = 20;" + lineSep +
					"GaugeDisplay[\"" + device + "\"].digitWidth      = 14;" + lineSep +
					"GaugeDisplay[\"" + device + "\"].digitDistance   = 2.5;" + lineSep +
					"GaugeDisplay[\"" + device + "\"].segmentWidth    = 2;" + lineSep +
					"GaugeDisplay[\"" + device + "\"].segmentDistance = 0.3;" + lineSep +
					"GaugeDisplay[\"" + device + "\"].segmentCount    = 7;" + lineSep +
					"GaugeDisplay[\"" + device + "\"].cornerType      = 3;" + lineSep +
					"GaugeDisplay[\"" + device + "\"].colorOn         = \"#e95d0f\";" + lineSep +
					"GaugeDisplay[\"" + device + "\"].colorOff        = \"#4b1e05\";" + lineSep +
					"GaugeDisplay[\"" + device + "\"].draw();" + lineSep;
			if(type.equals("PID")) {
				controller += "Gauges[\"" + device + "\"] = new JustGage({ id: \"" + device + "-gage\", min: 0, max:100, title:\"Cycle\" }); " + lineSep +
					"$(\"input[type='number']\").stepper();"+ lineSep;
			}
				
				controller += "});" + lineSep + lineSep +
					"</script>" + lineSep +
					"<div id=\"" + device + "-header\" class=\"header\"";
				if(type.equals("PID")) {
					controller += " onclick=\"toggleDiv('" + device + "-controls');\" "; 
				}
				controller += "><div id=\"" + device + "-title\" class=\"title\"" +
				
			
					">" + device + "</div>" + lineSep +
					" <canvas id=\"" + device + "-tempGauge\" class=\"gauge\" width=\"300\" height=\"140\">" + lineSep +
					"</canvas>" + lineSep +
					"<div id='" + device + "-tempSummary'>Temperature(<div id='tempUnit'>F</div>): " + lineSep +
							"<div id='" + device + "-tempStatus' >temp</div>&#176<div id='tempUnit'>F</div>" + lineSep + 
					"</div>" + lineSep +
					"</div>" + lineSep;
				
			if(type.equals("PID")) {
				controller += "<div id=\"" + device + "-controls\" class='controller'>" + lineSep + 	
					"<div id=\"" + device + "-gage\" class='gage'></div>" + lineSep +
					"<form id=\""+ device + "-form\" class=\"controlPanelForm\" >" + lineSep +
					"<div class=\"holo-buttons\">" + lineSep +
					"<button id=\"" + device + "-modeAuto\" class=\"holo-button modeclass holo-button\" onclick='disable(this); selectAuto(this); return false;'>Auto</button>" + lineSep +
					"<button id=\"" + device + "-modeManual\" class=\"holo-button modeclass\" onclick='disable(this); selectManual(this); return false;'>Manual</button>" + lineSep +
                    "<button id=\"" + device + "-modeOff\" class=\"holo-button modeclass\" onclick='disable(this); selectOff(this); return false;'>Off</button>" + lineSep +
                    "</div>" + lineSep +
                    
                    "<div id='pidInput' class='labels'>" +
                    	// 	SET POINT
                    	"<div id='"+ device + "-SP' class='holo-field'>" + lineSep +
	                    	"<div id='"+ device + "-labelSP' >Set Point:</div>"+ lineSep +
	                    	"<div id=\"" + device + "-setpoint\">" + lineSep +
	                    		"<input class='inputBox setpoint' type=\"text\" name=\"" + device + "-setpoint\"  maxlength = \"4\" size =\"4\" value=\"\" style=\"text-align: left;\"/>" +
	                    	"</div>" + lineSep +
	                    	"<div id='"+ device + "-unitSP'><div id='tempUnit'>F</div></div>"+ lineSep +
	                    	"</div>" + lineSep +
            
                    	// DUTY CYCLE
                    	"<br />" + "<div id='"+ device + "-DC' class='holo-field'>" + lineSep +
                    	"<div id='"+ device + "-labelDC' >Duty Cycle:</div>" + lineSep +
                    
                    		"<div id=\"" + device + "-dutycycle\">" + lineSep +
                    			"<input class='inputBox dutycycle' name=\"" + device + "-dutycycle\" maxlength = \"6\" size =\"6\" value=\"\" style=\"text-align: left;\"/>" +
                    		"</div>" + lineSep +
                    	"<div id='"+ device + "-unitDC'>%</div><br />"+ 
                    	"</div>" + lineSep +
                    	
                    	// DUTY TIME
                    	"<br />" + "<div id='"+ device + "-DT' class='holo-field'>" + lineSep +
                    	"<div id='"+ device + "-labelDT' >Duty Time:</div>" + lineSep +
                 			"<div id=\"" + device + "-cycletime\">" + lineSep +
                 				"<input class='inputBox dutytime' name=\"" + device + "-cycletime\" maxlength = \"6\" size =\"6\" value=\"\" style=\"text-align: left;\"/>" +
                 			"</div>" + lineSep +
                 		"<div id='"+ device + "-unitDT'>secs</div><br />"+ 
                 		"</div>" + lineSep +
                 		
                 		// P
	                    "<br />" + "<div id='"+ device + "-p' class='holo-field'>" + lineSep +
	                    "<div id='"+ device + "-labelp' >P:</div>" + lineSep +
		                    "<div id=\"" + device + "-pinput\">" + lineSep +
		                    "	<input class='inputBox p' name=\"" + device + "-p\"  maxlength = \"6\" size =\"6\" value=\"\" style=\"text-align: left;\"/>" +
		                    "</div>" + lineSep +
		                    "<div id='"+ device + "-unitP'>secs</div><br />"+ 
		                "</div>" + lineSep +
		                
		                // I
	                    "<br />" + "<div id='"+ device + "-i' class='holo-field'>" + lineSep +
	                    "<div id='"+ device + "-labeli' >I:</div>" + lineSep +
		                    "<div id=\"" + device + "-iinput\">" + lineSep +
		                    "	<input class='inputBox i' name=\"" + device + "-i\"  maxlength = \"6\" size =\"6\" value=\"\" style=\"text-align: left;\"/>" +
		                    "</div>" + lineSep +
		                    "<div id='"+ device + "-unitI'>&#176<div id='tempUnit'>F</div></div><br />" +
		                "</div>" + lineSep +    
		                // D
	                    "<br />" + "<div id='"+ device + "-d' class='holo-field'>" + lineSep +
	                    "<div id='"+ device + "-labeld' >D:</div>" + lineSep +
		        			"<div id=\"" + device + "-dinput\">" + lineSep +
				                    "	<input class='inputBox d ' name=\"" + device + "-d\"  maxlength = \"6\" size =\"6\" value=\"\" style=\"text-align: left;\"/>" +
							"</div>" + lineSep +
							"<div id='"+ device + "-unitD'>secs</div><br/>" +
						"</div>" + lineSep +	
					"</div>" + lineSep +

					"<div class='holo-buttons'>" + lineSep +
                    "<button class='holo-button' id=\"sendcommand\" type=\"submit\" value=\"SubmitCommand\" onClick='submitForm(this.form); waitForMsg(); return false;'>Send Command</button>" + lineSep +
                    "</div>" + lineSep +
					"</form>" + lineSep +
					"</div>" + lineSep; // finish off the Controller inputs
		}
		controller +=	"</div>";
		
		return controller;
	}

}
