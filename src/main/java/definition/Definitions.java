package definition;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by octav on 3/15/2017.
 */
@XmlRootElement( name = "DEFINITIONS" )
public class Definitions {
    private List<DefinitionEntry> definitionEntries;

    public List<DefinitionEntry> getDefinitionEntries() {
        return definitionEntries;
    }

    @XmlElement( name = "DEFINITION" )
    public void setDefinitionEntries(List<DefinitionEntry> definitionEntries) {
        this.definitionEntries = definitionEntries;
    }
}
