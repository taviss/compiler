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
    private TokenType name;
    private String regEx;
    private String type;
    private int priority;

    public TokenType getName() {
        return name;
    }

    @XmlValue
    public void setName(TokenType name) {
        this.name = name;
    }

    public String getRegEx() {
        return regEx;
    }

    @XmlAttribute( name = "exp")
    public void setRegEx(String regEx) {
        this.regEx = regEx;
    }

    public String getType() {
        return type;
    }

    @XmlAttribute( name = "type")
    public void setType(String type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    @XmlAttribute( name = "priority")
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
