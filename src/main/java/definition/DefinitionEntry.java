package definition;

import token.TokenType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by octav on 3/15/2017.
 */
@XmlRootElement(name = "DEFINITION")
public class DefinitionEntry {
    private String name;
    private String regEx;
    //FIXME type is not supposed to be TokenType, but double/text/long etc.
    private TokenType type;

    public String getName() {
        return name;
    }

    @XmlValue
    public void setName(String name) {
        this.name = name;
    }

    public String getRegEx() {
        return regEx;
    }

    @XmlAttribute( name = "exp")
    public void setRegEx(String regEx) {
        this.regEx = regEx;
    }

    public TokenType getType() {
        return type;
    }

    @XmlAttribute( name = "type")
    public void setType(TokenType type) {
        this.type = type;
    }
}
