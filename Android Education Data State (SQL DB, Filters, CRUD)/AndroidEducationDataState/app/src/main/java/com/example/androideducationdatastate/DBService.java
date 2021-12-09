package com.example.androideducationdatastate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class DBService extends SQLiteOpenHelper {

    public static final String T_COMPANY = "Company";
    public static final String T_PRODUCT = "Product";
    public static final String T_FOUNDER = "Founder";
    public static final String T_USER = "User";
    public static final String T_CF = "CF";
    public static final String T_CPF = "CPF";

    // Table Company
    public static final String id = "id";
    public static final String CompanyName = "CompanyName";
    public static final String FounderName = "FounderName";

    // Table Product
    public static final String ProductName = "ProductName";
    public static final String ProductPrice = "ProductPrice";
    public static final String ProductType = "ProductType";

    // Table T_CPF
    public static final String company_id = "id_company";
    public static final String founder_id = "id_founder";
    public static final String product_id = "id_product";

    // Table User
    public static final String Login = "UserLogin";
    public static final String Password = "UserPassword";

    public DBService(Context context){
        super(context, "app.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Table Company
        String query = "CREATE TABLE " + T_COMPANY + " " +
                "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CompanyName + " TEXT UNIQUE);";
        db.execSQL(query);

        // Create Table Founder
        query = "CREATE TABLE " + T_FOUNDER + " " +
                "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FounderName + " TEXT UNIQUE);";
        db.execSQL(query);

        // Create Table Product
        query =  "CREATE TABLE " + T_PRODUCT + " " +
                "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductName  + " TEXT UNIQUE, "
                + ProductPrice + " TEXT, "
                + ProductType  + " TEXT);";
        db.execSQL(query);

        // Create Table Company-Founder-Product
        query =  "CREATE TABLE " + T_CPF + " " +
                "(" + company_id + " INTEGER, "
                + founder_id + " INTEGER, "
                + product_id + " INTEGER, "
                + "FOREIGN KEY (" + company_id + ") REFERENCES " + T_COMPANY + " (" + id + "), "
                + "FOREIGN KEY (" + founder_id + ") REFERENCES " + T_FOUNDER + " (" + id + "), "
                + "FOREIGN KEY (" + product_id + ") REFERENCES " + T_PRODUCT + "(" + id + "));";
        db.execSQL(query);

        // Create Table Company-Founder
        query =  "CREATE TABLE " + T_CF + " " +
                "(" + company_id + " INTEGER, "
                + founder_id + " INTEGER, "
                + "FOREIGN KEY (" + company_id + ") REFERENCES " + T_COMPANY + " (" + id + "), "
                + "FOREIGN KEY (" + founder_id + ") REFERENCES " + T_FOUNDER + " (" + id + "));";
        db.execSQL(query);

        // Create Table User
        query = "CREATE TABLE " + T_USER + " " +
                "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Login + " TEXT UNIQUE, "
                + Password + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + T_COMPANY);
        db.execSQL("DROP TABLE IF EXISTS " + T_FOUNDER);
        db.execSQL("DROP TABLE IF EXISTS " + T_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + T_CPF);

        onCreate(db);
    }

    public ArrayList<CompanyItem> GetCompanies(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<CompanyItem> companies = new ArrayList<CompanyItem>();

        Cursor query = db.rawQuery("SELECT Company.CompanyName, Founder.FounderName FROM CF " +
                "JOIN Company ON Company.id = CF.id_company " +
                "JOIN Founder ON Founder.id = CF.id_founder " +
                "GROUP BY Company.CompanyName", null);
        while(query.moveToNext()){
            String companyName = query.getString(0);
            String companyFounder = query.getString(1);
            companies.add(new CompanyItem(companyName, companyFounder));
        }

        query.close();
        db.close();
        return companies;
    }

    public ArrayList<ProductItem> GetProducts(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<ProductItem> productItems = new ArrayList<ProductItem>();

        Cursor query = db.rawQuery("SELECT * FROM " + T_PRODUCT + ";", null);

        while(query.moveToNext()){
            String productName = query.getString(1);
            String productPrice = query.getString(2);
            String productType = query.getString(3);
            productItems.add(new ProductItem(productName, productPrice, productType));
        }

        db.close();
        return productItems;
    }

    public ArrayList<ProductItem> GetProducts(String company){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<ProductItem> productItems = new ArrayList<ProductItem>();

        Cursor query = db.rawQuery("SELECT Company.CompanyName, Product.ProductName, Product.ProductPrice, Product.ProductType" +
                " FROM CPF " +
                "JOIN Company ON Company.id = CPF.id_company " +
                "JOIN Product ON Product.id = CPF.id_product;", null);

        while(query.moveToNext()){
            if(query.getString(0).toString().equals(company)){
                String productName = query.getString(1);
                String productPrice = query.getString(2);
                String productType = query.getString(3);
                productItems.add(new ProductItem(productName, productPrice, productType));
            }
        }

        db.close();
        return productItems;
    }

    // Companies functions
    public void AddCompany(CompanyItem companyItem, ArrayList<ProductItem> productItems){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        ArrayList<Integer> productItemsID = new ArrayList<Integer>();
        long res = 0;
        int idCompany = -1, idFounder = -1;

        String companyName = (!companyItem.getCompanyName().equals("")) ? companyItem.getCompanyName() : "Компания";
        String companyFounder = (!companyItem.getCompanyFounderField().equals("")) ? companyItem.getCompanyFounderField() : "Отсутствует";

        values.put(CompanyName, companyName);
        res = db.insert(T_COMPANY, null, values);
        if(res == -1){
            throw new NullPointerException("Company is exists");
        }
        values.clear();

        values.put(FounderName, companyFounder);
        db.insert(T_FOUNDER,null, values);
        values.clear();

        Cursor query = db.rawQuery("SELECT * FROM " + T_COMPANY + ";", null);
        while (query.moveToNext()){
            if(query.getString(1).toString().equals(companyName)){
                idCompany = query.getInt(0);
                break;
            }
        }

        Cursor query_1 = db.rawQuery("SELECT * FROM " + T_FOUNDER + ";", null);
        while (query_1.moveToNext()){
            if(query_1.getString(1).toString().equals(companyFounder)){
                idFounder = query_1.getInt(0);
                break;
            }
        }

        values.clear();

        values.put(company_id, idCompany);
        values.put(founder_id, idFounder);
        res = db.insert(T_CF, null, values);
        values.clear();

        for(ProductItem item : productItems){
            int idProduct = -1;

            values.put(ProductName, item.getProductName());
            values.put(ProductPrice, item.getProductPrice());
            values.put(ProductType, item.getProductType());

            db.insert(T_PRODUCT, null, values);
            values.clear();

            Cursor query_2 = db.rawQuery("SELECT * FROM " + T_PRODUCT + ";", null);
            while (query_2.moveToNext()){
                if(query_2.getString(1).toString().equals(item.getProductName())){
                    idProduct = query_2.getInt(0);
                    break;
                }
            }

            values.put(company_id, idCompany);
            values.put(founder_id, idFounder);
            values.put(product_id, idProduct);
            res = db.insert(T_CPF, null, values);
            values.clear();
        }
        db.close();
    }
    public void ChangeCompany(CompanyItem oldCompany, CompanyItem newCompany){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        int idCompany = -1, idFounder = -1;
        Cursor query = db.rawQuery("SELECT * FROM " + T_COMPANY + ";", null);
        while (query.moveToNext()){
            if(query.getString(1).toString().equals(oldCompany.getCompanyName())){
                idCompany = query.getInt(0);
                break;
            }
        }

        Cursor query_1 = db.rawQuery("SELECT * FROM " + T_FOUNDER + ";", null);
        while (query_1.moveToNext()){
            if(query_1.getString(1).toString().equals(oldCompany.getCompanyFounderField())){
                idFounder = query_1.getInt(0);
                break;
            }
        }

        if(idCompany != -1) {
            values.put(CompanyName, newCompany.getCompanyName());
            db.update(T_COMPANY, values, id + "=?",new String[]{Integer.toString(idCompany)});
            values.clear();
        }

        if(idFounder != -1) {
            values.put(FounderName, newCompany.getCompanyFounderField());
            db.update(T_FOUNDER, values,id + "=?", new String[]{Integer.toString(idFounder)});
            values.clear();
        }

        db.close();
    }
    public void DeleteCompany(CompanyItem companyItem){
        String companyName = companyItem.getCompanyName();
        String companyFounder = companyItem.getCompanyFounderField();
        ArrayList<Integer> listProduct = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        long res = 0;
        int idCompany = -1, idFounder = -1;

        Cursor query = db.rawQuery("SELECT * FROM " + T_COMPANY + ";", null);
        while (query.moveToNext()){
            if(query.getString(1).toString().equals(companyName)){
                idCompany = query.getInt(0);
                break;
            }
        }

        Cursor query_1 = db.rawQuery("SELECT * FROM " + T_FOUNDER + ";", null);
        while (query_1.moveToNext()){
            if(query_1.getString(1).toString().equals(companyFounder)){
                idFounder = query_1.getInt(0);
                break;
            }
        }

        Cursor query_2 = db.rawQuery("SELECT * FROM " + T_CPF + ";", null);
        while (query_2.moveToNext()){
            if(query_2.getInt(0) == idCompany){
                listProduct.add(query_2.getInt(0));
            }
        }

        if(idCompany != -1) {
            db.delete(T_COMPANY, id + "=?", new String[]{Integer.toString(idCompany)});
            db.delete(T_CPF, company_id + "=?", new String[]{Integer.toString(idCompany)});
            db.delete(T_CF, company_id + "=?", new String[]{Integer.toString(idCompany)});

            for(int indexProduct : listProduct){
                db.delete(T_PRODUCT, id + "=?", new String[]{Integer.toString(indexProduct)});
            }
        }

        if(idFounder != -1) {
            db.delete(T_FOUNDER, id + "=?", new String[]{Integer.toString(idFounder)});
        }

        db.close();
    }
    public HashMap<String, Double> GetCompanyGeneralPriceProduct(String companyName){
        SQLiteDatabase db = this.getWritableDatabase();
        HashMap<String, Double> mapPrice = new HashMap<String, Double>();

        double generalPrice = 0;
        double avgPrice = 0;
        int countProduct = 0;
        int idCompany = -1;

        Cursor query = db.rawQuery("SELECT * FROM " + T_COMPANY + ";", null);
        while (query.moveToNext()){
            if(query.getString(1).toString().equals(companyName)){
                idCompany = query.getInt(0);
                break;
            }
        }

        query = db.rawQuery("SELECT Product.ProductPrice FROM CPF " +
                "JOIN Product ON Product.id = CPF.id_product Where CPF.id_company = " + idCompany, null);
        while(query.moveToNext()){
            generalPrice += Integer.parseInt(query.getString(0).toString().replaceAll(" ", ""));
            countProduct += 1;
        }

        avgPrice = generalPrice / countProduct;
        if (Double.isNaN(avgPrice)) {
            avgPrice = 0;
        }
        mapPrice.put("GeneralPrice", generalPrice);
        mapPrice.put("AvgPrice", avgPrice);

        db.close();
        return mapPrice;
    }

    // Product functions
    public void AddProduct(ProductItem productItem, CompanyItem company){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long res = 0;
        int idProduct = -1, idCompany = -1, idFounder = -1;

        values.put(ProductName, productItem.getProductName());
        values.put(ProductPrice, productItem.getProductPrice());
        values.put(ProductType, productItem.getProductType());

        res = db.insert(T_PRODUCT, null, values);
        if(res == -1){
            throw new NullPointerException("Product is exists");
        }

        Cursor query = db.rawQuery("SELECT * FROM " + T_PRODUCT + ";", null);
        while (query.moveToNext()){
            if(query.getString(1).toString().equals(productItem.getProductName())){
                idProduct = query.getInt(0);
                break;
            }
        }

        Cursor query_2 = db.rawQuery("SELECT * FROM " + T_COMPANY + ";", null);
        while (query_2.moveToNext()){
            if(query_2.getString(1).toString().equals(company.getCompanyName())){
                idCompany = query_2.getInt(0);
                break;
            }
        }

        Cursor query_3 = db.rawQuery("SELECT * FROM " + T_FOUNDER + ";", null);
        while (query_3.moveToNext()){
            if(query_3.getString(1).toString().equals(company.getCompanyFounderField())){
                idCompany = query_3.getInt(0);
                break;
            }
        }

        values.clear();

        values.put(company_id, idCompany);
        values.put(founder_id, idFounder);
        values.put(product_id, idProduct);
        res = db.insert(T_CPF, null, values);
        values.clear();

        db.close();
    }
    public void ChangeProduct(ProductItem oldProduct, ProductItem newProduct){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        int idProduct = -1;
        Cursor query = db.rawQuery("SELECT * FROM " + T_PRODUCT + ";", null);
        while (query.moveToNext()){
            if(query.getString(1).toString().equals(oldProduct.getProductName())){
                idProduct = query.getInt(0);
                break;
            }
        }

        if(idProduct != -1) {
            values.put(ProductName, newProduct.getProductName());
            values.put(ProductPrice, newProduct.getProductPrice());
            values.put(ProductType, newProduct.getProductType());
            db.update(T_PRODUCT, values, id + "=?",new String[]{Integer.toString(idProduct)});
            values.clear();
        }
        db.close();
    }
    public void DeleteProduct(ProductItem productItem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        long res = 0;
        int idProduct = -1;


        Cursor query_1 = db.rawQuery("SELECT * FROM " + T_PRODUCT + ";", null);
        while (query_1.moveToNext()){
            if(query_1.getString(1).toString().equals(productItem.getProductName().toString())){
                idProduct = query_1.getInt(0);
                break;
            }
        }

        if(idProduct != -1) {
            db.delete(T_PRODUCT, id + "=?", new String[]{Integer.toString(idProduct)});
            db.delete(T_CPF, product_id + "=?", new String[]{Integer.toString(idProduct)});
        }
        db.close();
    }

    // User functions
    public boolean Login(String login, String password){
        boolean flag = false;

        SQLiteDatabase db = this.getWritableDatabase();
        String hashPassword = md5Custom(password);
        Cursor query = db.rawQuery("SELECT * FROM " + T_USER + ";", null);

        while (query.moveToNext()){
            if(query.getString(1).toString().equals(login) &&
                    query.getString(2).toString().equals(hashPassword)){
                flag = true;
                break;
            }
        }
        return flag;
    }
    public boolean CheckLogin(String login){
        // True - isFree
        // False - isTacked

        boolean flag = true;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor query = db.rawQuery("SELECT * FROM " + T_USER + ";", null);

            while (query.moveToNext()) {
                if (query.getString(1).toString().equals(login)) {
                    flag = false;
                    break;
                }
            }
            return flag;
        }catch (Exception e){
            return true;
        }


    }
    public boolean Registration(String login, String password){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            String hashPassword = md5Custom(password);

            values.put(Login, login);
            values.put(Password, hashPassword);

            db.insert(T_USER, null, values);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static String md5Custom(String st) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            // тут можно обработать ошибку
            // возникает она если в передаваемый алгоритм в getInstance(,,,) не существует
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while( md5Hex.length() < 32 ){
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }
}
