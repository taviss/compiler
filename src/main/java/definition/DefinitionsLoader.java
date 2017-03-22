package definition;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Created by octav on 3/15/2017.
 */
public class DefinitionsLoader {
    private static final String DEFAULT_DEF_PATH = "definitions.xml";

    public Definitions loadDefinitions() {
        Definitions definitions = new Definitions();
        try {
            URL defURL = this.getClass().getClassLoader().getResource(DEFAULT_DEF_PATH);
            JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            definitions = (Definitions) jaxbUnmarshaller.unmarshal(defURL);
        } catch (JAXBException e) {
            System.out.println(e);
        }
        return definitions;
    }

    public Definitions loadDefinitions(String path) {
        Definitions definitions = new Definitions();
        try {
            File file = new File(path);
            JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            definitions = (Definitions) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            //TODO
        }
        return definitions;
    }
}
