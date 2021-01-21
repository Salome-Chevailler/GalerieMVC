/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package galerie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import galerie.dao.TableauRepository;
import galerie.entity.Tableau;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Salomé Chevailler
 */
@Controller
@RequestMapping(path = "/tableau")
public class TableauController {
    
    @Autowired
    private TableauRepository tableauDAO;
    
    @GetMapping(path = "show")
    public String afficheTousLesTableaux(Model model) {
        model.addAttribute("tableaux", tableauDAO.findAll());
        return "afficheTableaux";
    }
    
        /**
     * Montre le formulaire permettant d'ajouter un tableau
     *
     * @param tableau initialisé par Spring, valeurs par défaut à afficher dans le formulaire
     * @return le nom de la vue à afficher ('formulaireTableau.html')
     */
    @GetMapping(path = "add")
    public String montreLeFormulairePourAjout(@ModelAttribute("tableau") Tableau tableau) {
        return "formulaireTableau";
    }
    
        /**
     * Appelé par 'formulaireTableau.html', méthode POST
     *
     * @param tableau Un tableau initialisé avec les valeurs saisies dans le formulaire
     * @param redirectInfo pour transmettre des paramètres lors de la redirection
     * @return une redirection vers l'affichage de la liste des tableaux
     */
    @PostMapping(path = "save")
    public String ajouteLeTableauPuisMontreLaListe(Tableau tableau, RedirectAttributes redirectInfo) {
        String message;
        try {
            // cf. https://www.baeldung.com/spring-data-crud-repository-save
            tableauDAO.save(tableau);
            // L'id du tableau a été initialisé par la BD au moment de l'insertion
            message = "Le tableau '" + tableau.getTitre() + "' a été correctement enregistré";
        } catch (DataIntegrityViolationException e) {
            // Les titres sont définis comme 'UNIQUE' 
            // En cas de doublon, JPA lève une exception de violation de contrainte d'intégrité
            message = "Erreur : Le tableau '" + tableau.getTitre() + "' existe déjà";
        }
        // RedirectAttributes permet de transmettre des informations lors d'une redirection,
        // Ici on transmet un message de succès ou d'erreur
        // Ce message est accessible et affiché dans la vue 'afficheTableau.html'
        redirectInfo.addFlashAttribute("message", message);
        return "redirect:show"; // POST-Redirect-GET : on se redirige vers l'affichage de la liste		
    }
    
    /**
     * Appelé par le lien 'Supprimer' dans 'afficheTableaux.html'
     *
     * @param tableau à partir de l'id dy tableau transmis en paramètre, Spring fera une requête SQL SELECT pour
     * chercher le tableau dans la base
     * @param redirectInfo pour transmettre des paramètres lors de la redirection
     * @return une redirection vers l'affichage de la liste des tableaux
     */
    @GetMapping(path = "delete")
    public String supprimeUnTableauPuisMontreLaListe(@RequestParam("id") Tableau tableau, RedirectAttributes redirectInfo) {
        String message = "Le tableau '" + tableau.getTitre() + "' a bien été supprimé";
        try {
            tableauDAO.delete(tableau); // Ici on peut avoir une erreur
        } catch (DataIntegrityViolationException e) {
            // violation de contrainte d'intégrité 
            message = "Erreur : Impossible de supprimer le tableau '" + tableau.getTitre() + "' !";
        }
        // RedirectAttributes permet de transmettre des informations lors d'une redirection,
        // Ici on transmet un message de succès ou d'erreur
        // Ce message est accessible et affiché dans la vue 'afficheTableau.html'
        redirectInfo.addFlashAttribute("message", message);
        return "redirect:show"; // on se redirige vers l'affichage de la liste
    }
    
}
