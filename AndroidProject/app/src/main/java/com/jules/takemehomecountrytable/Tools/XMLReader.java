package com.jules.takemehomecountrytable.Tools;

import android.content.Context;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;

import com.jules.takemehomecountrytable.Fragments.Map.Room.FrontDoor;
import com.jules.takemehomecountrytable.Fragments.Map.Room.OfficeRoom;
import com.jules.takemehomecountrytable.Fragments.Map.Room.Room;
import com.jules.takemehomecountrytable.Fragments.Map.Room.RoomType;
import com.jules.takemehomecountrytable.Fragments.Map.Room.Stair;
import com.jules.takemehomecountrytable.Fragments.Map.Room.TdRoom;
import com.jules.takemehomecountrytable.Fragments.Map.Room.TpRoom;
import com.jules.takemehomecountrytable.Fragments.Map.Room.WC;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLReader {

    public static ArrayList CreateXMLData(Context context) throws FileNotFoundException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        ArrayList<ArrayList<Room>> listOfFloor = new ArrayList<>();

        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(new File(context.getFilesDir(), "map.xml"));
            final Element racine = document.getDocumentElement();

            final NodeList racineNoeuds = racine.getChildNodes();
            ArrayList<Room> listOfRoomPos0 = new ArrayList<>();
            ArrayList<Room> listOfRoomPos1 = new ArrayList<>();
            //PERMET DE RECUPERER LES ELEMENTS LES PERES DE TOUTES CHOSES

            Log.d("Taille du noeud : ", String.valueOf(racineNoeuds.getLength()));

            for (int i = 0; i < racineNoeuds.getLength(); i++) {
                if (racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    final Element room = (Element) racineNoeuds.item(i);
                    NodeList nums = room.getElementsByTagName("num");
                    NodeList names = room.getElementsByTagName("name");
                    NodeList poss = room.getElementsByTagName("pos");
                    NodeList namesProf = room.getElementsByTagName("nameProf");
                    ArrayList<String> listProf = new ArrayList<>();


                    Element name = null;
                    Element num = null;
                    Element pos = null;
                    Element prof = null;

                    for (int y = 0; y < names.getLength(); y++) {
                        name = (Element) names.item(y);
                    }

                    for (int y = 0; y < nums.getLength(); y++) {
                        num = (Element) nums.item(y);
                    }

                    for (int y = 0; y < poss.getLength(); y++) {
                        pos = (Element) poss.item(y);
                    }

                    for (int y = 0; y < namesProf.getLength(); y++) {
                        prof = (Element) namesProf.item(y);
                        listProf.add(prof.getTextContent());
                    }

                    switch (room.getAttribute("type").toUpperCase()) {
                        case "TD":
                            if (name != null) {
                                if (Integer.valueOf(pos.getTextContent()) == 0)
                                    listOfRoomPos0.add(new TdRoom(RoomType.TD, num.getTextContent(), name.getTextContent(), Integer.valueOf(pos.getTextContent())));
                                if (Integer.valueOf(pos.getTextContent()) == 1)
                                    listOfRoomPos1.add(new TdRoom(RoomType.TD, num.getTextContent(), name.getTextContent(), Integer.valueOf(pos.getTextContent())));
                            } else if (Integer.valueOf(pos.getTextContent()) == 0)
                                listOfRoomPos0.add(new TdRoom(RoomType.TD, num.getTextContent(), null, Integer.valueOf(pos.getTextContent())));
                            if (Integer.valueOf(pos.getTextContent()) == 1)
                                listOfRoomPos1.add(new TdRoom(RoomType.TD, num.getTextContent(), null, Integer.valueOf(pos.getTextContent())));
                            break;
                        case "TP":
                            if (Integer.valueOf(pos.getTextContent()) == 0)
                                listOfRoomPos0.add(new TpRoom(RoomType.TP, num.getTextContent(), name.getTextContent(), Integer.valueOf(pos.getTextContent())));
                            if (Integer.valueOf(pos.getTextContent()) == 1)
                                listOfRoomPos1.add(new TpRoom(RoomType.TP, num.getTextContent(), name.getTextContent(), Integer.valueOf(pos.getTextContent())));

                            break;

                        case "BUREAU":
                            if (Integer.valueOf(pos.getTextContent()) == 0)
                                listOfRoomPos0.add(new OfficeRoom(RoomType.BUREAU, listProf, Integer.valueOf(pos.getTextContent())));
                            if (Integer.valueOf(pos.getTextContent()) == 1)
                                listOfRoomPos1.add(new OfficeRoom(RoomType.BUREAU, listProf, Integer.valueOf(pos.getTextContent())));
                            break;

                        case "WC":
                            if (Integer.valueOf(pos.getTextContent()) == 0)
                                listOfRoomPos0.add(new WC(Integer.valueOf(pos.getTextContent())));
                            if(Integer.valueOf(pos.getTextContent()) == 1)
                                listOfRoomPos1.add(new WC(Integer.valueOf(pos.getTextContent())));
                            break;

                        case "STAIR":
                            if (Integer.valueOf(pos.getTextContent()) == 0)
                                listOfRoomPos0.add(new Stair(Integer.valueOf(pos.getTextContent())));
                            if(Integer.valueOf(pos.getTextContent()) == 1)
                                listOfRoomPos1.add(new Stair(Integer.valueOf(pos.getTextContent())));
                            break;

                        case "FRONTDOOR":
                            if (Integer.valueOf(pos.getTextContent()) == 0)
                                listOfRoomPos0.add(new FrontDoor(Integer.valueOf(pos.getTextContent())));
                            if(Integer.valueOf(pos.getTextContent()) == 1)
                                listOfRoomPos1.add(new FrontDoor(Integer.valueOf(pos.getTextContent())));
                            break;

                    }

                    listOfFloor.add(listOfRoomPos0);
                    listOfFloor.add(listOfRoomPos1);
                }
            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return listOfFloor;
    }
}
