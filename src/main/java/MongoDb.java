import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class MongoDb {

    public static void main(String[] args) {

        // Connect to local mongodb server
        MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
        MongoClient client = new MongoClient(uri);

        // Connect to a database named "codethen"
        MongoDatabase db = client.getDatabase("codethen");

        // Get collection called "products"
        MongoCollection<Document> products = db.getCollection("products");

        System.out.println(getById(products, new ObjectId("59ff4fe4d56a830e40d0abcb")).getName());

        List<Product> productos = getAll(products);

        System.out.println(productos.get(0).getName());

        Product producto = new Product("ps4", 300.0);

        addProduct(products, producto);

        removeProduct(products, new ObjectId("59ff4fe4d56a830e40d0abcb"));

        Product producto1 = new Product("ps3", 90.0);

        editProduct(products, new ObjectId("59ff645ef991ce16a85c1707"), producto1);

        client.close();

    }

    static Product getById (MongoCollection<Document> products, ObjectId id) {

        Bson query = eq("_id", id);
        MongoCursor<Document> cursor = products.find(query).iterator();
        Product producto = new Product();

        while (cursor.hasNext()) {

            Document doc = cursor.next();
            String name = (String) doc.get("name");
            double price = (Double) doc.get("price");
            producto = new Product(name, price);
        }
        return producto;
    }

    static List<Product> getAll (MongoCollection<Document> products) {

        MongoCursor<Document> cursor = products.find().iterator();
        Product producto = new Product();
        List<Product> productos = new ArrayList<>();

        while (cursor.hasNext()) {

            Document doc = cursor.next();
            String name = (String) doc.get("name");
            double price = (Double) doc.get("price");
            producto = new Product(name, price);
            productos.add(producto);
        }
        return productos;
    }

    static void addProduct (MongoCollection<Document> products, Product producto) {

        Document document = new Document("name", producto.getName()).append("price", producto.getPrice());
        products.insertOne(document);
    }

    static void removeProduct (MongoCollection<Document> products, ObjectId id) {

        products.deleteOne(eq("_id", id));
    }

    static void editProduct (MongoCollection<Document> products, ObjectId id, Product producto) {

        products.updateOne(eq("_id", id), combine(set("name", producto.getName()), set("price", producto.getPrice())));
    }
}
