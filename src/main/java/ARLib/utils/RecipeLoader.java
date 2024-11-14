package ARLib.utils;

import net.minecraft.client.Minecraft;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeLoader {

    public static List<MachineRecipe> loadRecipes( Path configDir, String filename) {
        List<MachineRecipe> recipes = new ArrayList<>();

        try {
            // Define the config directory path
            Path filePath = configDir.resolve(filename);

            // Create the directory if it doesn’t exist
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }

            // Load the XML file
            InputStream xmlFile = Files.newInputStream(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();
            NodeList recipeNodes = doc.getElementsByTagName("recipe");

            for (int i = 0; i < recipeNodes.getLength(); i++) {
                Node recipeNode = recipeNodes.item(i);

                if (recipeNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element recipeElement = (Element) recipeNode;

                    MachineRecipe recipe = new MachineRecipe();

                    // Parse input items
                    NodeList inputItemsNodes = recipeElement.getElementsByTagName("inputItems").item(0).getChildNodes();
                    for (int j = 0; j < inputItemsNodes.getLength(); j++) {
                        Node itemNode = inputItemsNodes.item(j);
                        if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element itemElement = (Element) itemNode;
                            String id = itemElement.getAttribute("id");
                            int amount = Integer.parseInt(itemElement.getAttribute("amount"));
                            recipe.addInput(id, amount);
                        }
                    }

                    // Parse output items
                    NodeList outputItemsNodes = recipeElement.getElementsByTagName("outputItems").item(0).getChildNodes();
                    for (int j = 0; j < outputItemsNodes.getLength(); j++) {
                        Node itemNode = outputItemsNodes.item(j);
                        if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element itemElement = (Element) itemNode;
                            String id = itemElement.getAttribute("id");
                            int amount = Integer.parseInt(itemElement.getAttribute("amount"));
                            recipe.addOutput(id, amount);
                        }
                    }

                    // Parse energy and ticks
                    recipe.energyPerTick = Integer.parseInt(recipeElement.getElementsByTagName("energyPerTick").item(0).getTextContent());
                    recipe.ticksRequired = Integer.parseInt(recipeElement.getElementsByTagName("ticksRequired").item(0).getTextContent());

                    recipes.add(recipe);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return recipes;
    }



    public static void createRecipeFile(Path configDir, String filename, List<MachineRecipe> recipes) {
        Path filePath = configDir.resolve(filename);
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // Root element <recipes>
            Element rootElement = doc.createElement("recipes");
            doc.appendChild(rootElement);

            for (MachineRecipe recipe : recipes) {
                // <recipe>
                Element recipeElement = doc.createElement("recipe");
                rootElement.appendChild(recipeElement);

                // <inputItems> - Loop over the inputs map
                Element inputItems = doc.createElement("inputItems");
                recipeElement.appendChild(inputItems);
                for (Map.Entry<String, Integer> entry : recipe.inputs.entrySet()) {
                    Element inputItem = doc.createElement("item");
                    inputItem.setAttribute("id", entry.getKey());
                    inputItem.setAttribute("amount", String.valueOf(entry.getValue()));
                    inputItems.appendChild(inputItem);
                }

                // <outputItems> - Loop over the outputs map
                Element outputItems = doc.createElement("outputItems");
                recipeElement.appendChild(outputItems);
                for (Map.Entry<String, Integer> entry : recipe.outputs.entrySet()) {
                    Element outputItem = doc.createElement("item");
                    outputItem.setAttribute("id", entry.getKey());
                    outputItem.setAttribute("amount", String.valueOf(entry.getValue()));
                    outputItems.appendChild(outputItem);
                }

                // <energyPerTick>
                Element energyPerTick = doc.createElement("energyPerTick");
                energyPerTick.appendChild(doc.createTextNode(String.valueOf(recipe.energyPerTick)));
                recipeElement.appendChild(energyPerTick);

                // <ticksRequired>
                Element ticksRequired = doc.createElement("ticksRequired");
                ticksRequired.appendChild(doc.createTextNode(String.valueOf(recipe.ticksRequired)));
                recipeElement.appendChild(ticksRequired);
            }

            // Write the content to XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileOutputStream(filePath.toFile()));
            transformer.transform(source, result);

            System.out.println("Recipe XML file created at: " + filePath.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
