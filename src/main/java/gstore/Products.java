package gstore;


import gstore.products.Product;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.List;

@XmlRootElement(name="products")
@XmlAccessorType(XmlAccessType.FIELD)
public class Products {

    private final static String xml_file = "/Users/kevin/IdeaProjects/GoogleStoreQA/src/main/java/gstore/products/products.xml";

    private List<Product> _products = null;

    public Products() { }

    public static File GetXMLFile() {
        return new File(xml_file);
    }

    //@XmlElementWrapper
    @XmlElement(name="product")
    public List<Product> getProducts() {
        return _products;
    }

    /**
     * Required by JAXB for importing data from XML file
     * @param products
     */
    public void setProducts(List<Product> products) {
        this._products = products;
    }

}