package com.example.start.finalproject2;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by 100461439 on 11/30/2015.
 */
public class QueryDirections extends AsyncTask<String, Void, String> {
    private DirectionListener listener = null;
    private Exception exception = null;
    private String destinations = "";

    public QueryDirections(DirectionListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            // parse out the data
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            Log.d("InternetResourcesSample", "url: " + params[0]);
            URL url = new URL(params[0]);
            Document document = docBuilder.parse(url.openStream());
            document.getDocumentElement().normalize();
            //get all the nodes
            NodeList main = document.getElementsByTagName("DirectionsResponse");
            //if the nodelist consists of node objects
            if((main.getLength() > 0) && (main.item(0).getNodeType() == Node.ELEMENT_NODE)){
                //definitions is the root node
                Element definitions = (Element)main.item(0);
                NodeList defTags = definitions.getElementsByTagName("end_location");
                if (defTags == null){
                    defTags = definitions.getElementsByTagName("error_message");
                    Log.d("error", defTags.item(0).getTextContent());
                }else {
                    for (int i = 0; i < defTags.getLength(); i++) {
                        Node def = defTags.item(i).getChildNodes().item(1);
                        Node def2 = defTags.item(i).getChildNodes().item(3);
                        //add the lattitude and longitude to the array list
                        destinations += (def.getTextContent() + "," + def2.getTextContent() + ";");
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }

        return destinations;
    }

    @Override
    protected void onPostExecute(String result) {
        if (exception != null) {
            exception.printStackTrace();
            return;
        }

        Log.d("InternetResourcesSample", "setting definition: " + destinations);
        listener.showDirections(destinations);
    }
}