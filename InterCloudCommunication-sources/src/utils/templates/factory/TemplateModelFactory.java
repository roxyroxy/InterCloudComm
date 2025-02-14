/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.templates.factory;

import java.util.List;
import models.Disk;
import org.opennebula.client.OneResponse;
import org.opennebula.client.template.Template;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import models.Graphics;
import models.Network;
import models.OS;
import models.TemplateModel;
import utils.parsers.OneResponseParser;

/**
 *
 * @author oneadmin
 */
public class TemplateModelFactory {

    public TemplateModel createTemplateModel(Template template) {

        OneResponse templateResponse = template.info();
        String templateDescription = templateResponse.getMessage();
        TemplateModel tm = new TemplateModel();
        Document doc;

        try {
            doc = OneResponseParser.loadXMLFromString(templateDescription);

            NodeList nodeList = doc.getElementsByTagName("VMTEMPLATE");

            Node node = nodeList.item(0);
            Element templateElement = (Element) node;

            tm.setName(OneResponseParser.getValues("NAME", templateElement).get(0));
            tm.setCpu(Integer.parseInt(OneResponseParser.getValues("CPU", templateElement).get(0)));
            tm.setMemory(Integer.parseInt(OneResponseParser.getValues("MEMORY", templateElement).get(0)));

            Graphics graphics = new Graphics(
                    OneResponseParser.getValues("LISTEN", templateElement).get(0),
                    OneResponseParser.getValues("TYPE", templateElement).get(0));

            Network nic = new Network(
                    Integer.parseInt(
                            OneResponseParser.getValues("NETWORK_ID", templateElement).get(0)));
            OS os = new OS(
                    OneResponseParser.getValues("ARCH", templateElement).get(0),
                    OneResponseParser.getValues("BOOT", templateElement).get(0));
            
            List<Disk> disks = OneResponseParser.getDisksWithImages("IMAGE_ID", templateElement);
            tm.setDisks(disks);
            tm.setGraphics(graphics);
            tm.setNic(nic);
            tm.setOs(os);

        } catch (Exception ex) {
            System.out.println("Error while parsing the template FILE\n" + ex.getMessage());
        }
        return tm;
    }
}
