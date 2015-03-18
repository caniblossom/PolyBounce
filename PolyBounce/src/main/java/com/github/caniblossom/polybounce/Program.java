/*
 * Copyright (c) 2015, Jani Salo
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.caniblossom.polybounce;

import com.github.caniblossom.polybounce.game.Game;
import com.github.caniblossom.polybounce.math.Intersection;
import com.github.caniblossom.polybounce.math.Segment2;
import com.github.caniblossom.polybounce.math.Vector2;
import java.util.Scanner;

// TODO Add Checkstyle to the project.

/**
 * Main class.
 * @author Jani Salo
 */
public class Program {
    /**
     * Program entry point.
     * @param args arguments for execution
     */
    public static void main(String [] args) {
        System.out.println("Pitää lähteä loppuviikosta muualle, niin en ehtinyt tekemään mitään oikeasti interaktiivista settiä vielä.");
        System.out.println("Disabloin tilapäisesti tuon OpenGL canvaksen, ja tein tämmöisen pienen loopin jossa voi kokeilla Segment2 luokan intersect -metodia.\n");
        System.out.println("Huomaa että janat jotka ovat samalla suoralla eivät tässä implementaatiossa koskaan leikkaa toisiaan (jos leikkaavat niin se on bugi).");
        System.out.println("Tätä erikoistapausta ei huomioida, koska se ei enginen kannalta ole tarpeen ja tekisi asioista vain turhaan monimutkaisempia (ja on kiire).\n");

        // TODO Write render mechanism and code for rendering convex polygons.
        // final Game game = new Game();
        // game.run();

        // Huom! Sori ruma copypastekoodi, kiire + tämä tulee kuitenkin poistettua heti ensi viikolla.
        Scanner scanner = new Scanner(System.in);
                
        while (true) {
            System.out.println("Kirjoita \"pois\" lopettaaksesi ohjelman.");
            
            try {
                System.out.print("Anna ensimmäisen jana muodossa (x0, y0, x1, y1) ilman sulkuja: ");

                String s0 = scanner.nextLine();
                String[] s0Values = s0.split(",");

                if (s0Values.length != 4) {
                    if (s0Values.length == 1) {
                        if (s0Values[0].toLowerCase().equals("pois")) {
                            break;
                        }
                    }
                    
                    System.out.println("Väärä määrä parametrejä, yritä uudestaan!");
                    continue;
                } 
                
                final float ax = Float.parseFloat(s0Values[0]);
                final float ay = Float.parseFloat(s0Values[1]);
                final float bx = Float.parseFloat(s0Values[2]);
                final float by = Float.parseFloat(s0Values[3]);

                System.out.print("Anna toinen jana muodossa (x0, y0, x1, y1) ilman sulkuja: ");

                String s1 = scanner.nextLine();
                String[] s1Values = s1.split(",");

                if (s1Values.length != 4) {
                    if (s1Values.length == 1) {
                        if (s1Values[0].toLowerCase().equals("pois")) {
                            break;
                        }
                    }

                    System.out.println("Väärä määrä lukuja, yritä uudestaan!");
                    continue;
                } 
                
                final float cx = Float.parseFloat(s1Values[0]);
                final float cy = Float.parseFloat(s1Values[1]);
                final float dx = Float.parseFloat(s1Values[2]);
                final float dy = Float.parseFloat(s1Values[3]);
                
                final Vector2 a = new Vector2(ax, ay);
                final Vector2 b = new Vector2(bx, by);
                final Vector2 c = new Vector2(cx, cy);
                final Vector2 d = new Vector2(dx, dy);
                
                final Segment2 ab = new Segment2(a, b);
                final Segment2 cd = new Segment2(c, d);
                final Intersection i = ab.intersect(cd);

                System.out.println("Ensimmäinen jana: " + ab);
                System.out.println("Toinen jana: " + cd);
                
                if (i.didIntersect()) {
                    System.out.println("Janat leikkaavat pisteessä: (" + i.getPosition().getX() + ", " + i.getPosition().getY() + ")");
                } else {
                    System.out.println("Janat eivät leikkaa toisiaan.");
                } 

                System.out.println("");
            } catch (NumberFormatException e) {
                System.out.println("Sähelsit jotain, yritä uudestaan!");
            }
        }
    }   
}
