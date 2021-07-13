package db;

import Model.EquipmentBorrowedModel;
import Model.EquipmentReturnedModel;
import Model.EquipmentModel;
import Model.MembershipModel;

import java.util.ArrayList;

public class DB {
    public static ArrayList<MembershipModel> members = new ArrayList<>();
    public static ArrayList<EquipmentModel> equipment = new ArrayList<>();
    public static ArrayList<EquipmentBorrowedModel> borrowed = new ArrayList<>();
    public static ArrayList<EquipmentReturnedModel> returned = new ArrayList<>();
}
