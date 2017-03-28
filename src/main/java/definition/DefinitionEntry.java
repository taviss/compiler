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
    /**
     * The name/type of the token(definition)
     */
    private TokenType name;

    /**
     * The regEx string
     */
    private String regEx;

    /**
     * The type of the token. This is the group of the TokenType[Double, Long, Text]
     */
    private String type;

    /**
     * The priority of the token. This is used when multiple matches are found, so we can pick the "strongest" one
     */
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
