package definition;

import token.TokenType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by octav on 3/15/2017.
 *
 * A class that contains a list of entries. Each of these entries describe a "definition" of a token, represented by a regEx and some other
 * parameters for later use
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

    public DefinitionEntry getByName(TokenType tokenType) {
        for(DefinitionEntry definitionEntry : definitionEntries) {
            if(definitionEntry.getName() == tokenType) {
                return definitionEntry;
            }
        }
        return null;
    }
}
