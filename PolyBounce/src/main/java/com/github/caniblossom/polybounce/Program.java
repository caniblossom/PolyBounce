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
        System.out.println("Moi taas, en tiedä lukeeko kukaan näitä, mutta jätetään tähän kuitenkin viesti kaiken varalta.");
        System.out.println("Keskityin tällä viikolla siihen, että saisin renderöintipipelinen päällisin puolin toimimaan, koska se on hyvä saada pois alta.");
        System.out.println("Tämän takia en ole kirjoittanut uusia testejä ja koodi on muutenkin epäsiistiä paikoitellen.");
        System.out.println("Keskityn ensi viikolla testien ja dokumentaation kirjoitteluun, sekä nykyisen koodin viimeistelyyn.");
        System.out.println("Jos ette jostain syystä saa projektia LWJGL:n tai rautatuen puutteen takia toimimaan, niin pistäkään palautetta.");
        System.out.println("Tällä hetkellä ohjelma ei siis tee mitään muuta kuin renderöi monikulmaisen pitkulan ruudulla heilumassa.");
        System.out.println("Tästä on myös pieni video media -kansiossa, ihan vain kaiken varalta.");
        System.out.println("OpenGL koodi on tällä hetkellä hieman höttöä ja ei mm. tarkista että riittävä tuki löytyy / saattaa kutsua API:a ilman kontekstia.");
        System.out.println("Tästä saattaa seurata segfaulttailua quitatessa tai (toivottavasti ei) jotain muuta jännää. Korjaan nämä kyllä.");
        
        final Game game = new Game();
        game.run();
    }   
}
