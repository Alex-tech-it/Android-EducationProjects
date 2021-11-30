package com.example.androideducationdatastate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class DBService extends SQLiteOpenHelper {

    public static final String T_COMPANY = "Company";
    public static final String T_PRODUCT = "Product";
    public static final String T_FOUNDER = "Founder";
    public static final String T_CPF = "CPF";

    public static final String id = "id";
    public static final String CompanyName = "CompanyName";
    public static final String FounderName = "FounderName";
    public static final String ProductName = "ProductName";

    public static final String company_id = "id_company";
    public static final String founder_id = "id_founder";
    public static final String product_id = "id_product";

    public DBService(Context context){
        super(context, "app.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + T_COMPANY + " " +
                "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CompanyName + " TEXT UNIQUE);";
        db.execSQL(query);

        query = "CREATE TABLE " + T_FOUNDER + " " +
                "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FounderName + " TEXT UNIQUE);";
        db.execSQL(query);

        query =  "CREATE TABLE " + T_PRODUCT + " " +
                "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductName + " TEXT UNIQUE);";
        db.execSQL(query);

        query =  "CREATE TABLE " + T_CPF + " " +
                "(" + company_id + " INTEGER, "
                + founder_id + " INTEGER, "
                + product_id + " INTEGER, "
                + "FOREIGN KEY (" + company_id + ") REFERENCES " + T_COMPANY + " (" + id + "), "
                + "FOREIGN KEY (" + founder_id + ") REFERENCES " + T_FOUNDER + " (" + id + "), "
                + "FOREIGN KEY (" + product_id + ") REFERENCES " + T_PRODUCT + "(" + id + "));";
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

        Cursor query = db.rawQuery("SELECT * FROM CPF " +
                "JOIN Company ON Company.id = CPF.id_company " +
                "JOIN Founder ON Founder.id = CPF.id_founder " +
                "JOIN Product ON Product.id = CPF.id_product;", null);
        while(query.moveToNext()){
            String companyName = query.getString(4);
            String companyFounder = query.getString(6);
            String companyProduct = query.getString(8);
            companies.add(new CompanyItem(companyName, companyFounder, companyProduct));
        }

        query.close();
        db.close();
        return companies;
    }

    public void AddCompany(CompanyItem companyItem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        long res = 0;
        int idCompany = -1, idFounder = -1, idProduct = -1;

        String companyName = (!companyItem.getCompanyName().equals("")) ? companyItem.getCompanyName() : "Компания";
        String companyFounder = (!companyItem.getCompanyFounderField().equals("")) ? companyItem.getCompanyFounderField() : "Отсутствует";
        String companyProduct = (!companyItem.getCompanyProductField().equals("")) ? companyItem.getCompanyProductField() : "Отсутствует";

        values.put(CompanyName, companyName);
        res = db.insert(T_COMPANY, null, values);
        if(res == -1){
            throw new NullPointerException("Company is esxists");
        }
        values.clear();

        if(res != -1){
            values.put(FounderName, companyFounder);
            long res_2 = db.insert(T_FOUNDER, null, values);
            values.clear();

            if(res_2 != -1){
                values.put(ProductName, companyProduct);
                db.insert(T_PRODUCT, null, values);
                values.clear();
            }
        }

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

        Cursor query_2 = db.rawQuery("SELECT * FROM " + T_PRODUCT + ";", null);
        while (query_2.moveToNext()){
            if(query_2.getString(1).toString().equals(companyProduct)){
                idProduct = query_2.getInt(0);
                break;
            }
        }

        values.put(company_id, idCompany);
        values.put(founder_id, idFounder);
        values.put(product_id, idProduct);
        res = db.insert(T_CPF, null, values);
        values.clear();

        db.close();
    }

    public void ChangeCompany(CompanyItem oldCompany, CompanyItem newCompany){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        int idCompany = -1, idFounder = -1, idProduct = -1;
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

        Cursor query_2 = db.rawQuery("SELECT * FROM " + T_PRODUCT + ";", null);
        while (query_2.moveToNext()){
            if(query_2.getString(1).toString().equals(oldCompany.getCompanyProductField())){
                idProduct = query_2.getInt(0);
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

        if(idProduct != -1) {
            values.put(ProductName, newCompany.getCompanyProductField());
            db.update(T_PRODUCT, values,id + "=?", new String[]{Integer.toString(idProduct)});
            values.clear();
        }
    }

    public void DeleteCompany(CompanyItem companyItem){
        String companyName = companyItem.getCompanyName();
        String companyFounder = companyItem.getCompanyFounderField();
        String companyProduct = companyItem.getCompanyProductField();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        long res = 0;
        int idCompany = -1, idFounder = -1, idProduct = -1;

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

        Cursor query_2 = db.rawQuery("SELECT * FROM " + T_PRODUCT + ";", null);
        while (query_2.moveToNext()){
            if(query_2.getString(1).toString().equals(companyProduct)){
                idProduct = query_2.getInt(0);
                break;
            }
        }

        if(idCompany != -1) {
            db.delete(T_COMPANY, id + "=?", new String[]{Integer.toString(idCompany)});
            db.delete(T_CPF, company_id + "=?", new String[]{Integer.toString(idCompany)});
        }

        if(idFounder != -1) {
            db.delete(T_FOUNDER, id + "=?", new String[]{Integer.toString(idFounder)});
        }

        if(idProduct != -1) {
            db.delete(T_PRODUCT, id + "=?", new String[]{Integer.toString(idProduct)});
        }

        db.close();
    }
}
