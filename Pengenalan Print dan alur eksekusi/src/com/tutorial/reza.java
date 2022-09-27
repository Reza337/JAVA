package com.tutorial;
import java.util.Scanner;
public class reza {
    public static void main(String[] args) {
        int A, B;
        double beli, disc, total;

        Scanner keyboard = new Scanner(System.in);
        System.out.print("Harga barang: ");
        A = keyboard.nextInt();

        System.out.print("Jumlah barang: ");
        B = keyboard.nextInt();

        beli = A * B;

        if(B>100){
            disc = 0.15 * beli;
        }
        else{
            disc = 0.05 * beli;
        }

        total = beli - disc;

        System.out.println("Harga beli = Rp. " + beli);
        System.out.println("Harga diskon = Rp. " + disc);
        System.out.println("Total bayar = Rp. " + total);
    }
}
