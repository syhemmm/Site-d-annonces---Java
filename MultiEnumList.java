// Siham ait bennour 12308833
//je declare quil sagit de mon propre travail

package minebay;


import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

/**
 * Collection ordonnée, dont les éléments sont instances de classes implémentant
 * les interfaces Categorized et Comparable. Une MultiEnumList ne peut pas
 * contenir null.
 *
 * <p>
 * Une MultiEnumList prend en compte les catégories de ses éléments en
 * définissant pour certaines méthodes des versions alternatives opérant
 * uniquement sur un ensemble de catégories donné en argument de ces méthodes.
 * Cependant les méthodes héritées de l'interface Collection s'appliquent
 * toujours sur l'ensemble des éléments quelque soit leur catégorie.
 * </p>
 *
 * <p>
 * Dans ce but, MultiEnumList utilise une liste distincte (une LinkedList) pour
 * chaque catégorie. Les opérations utilisant un index (i.e. les deux méthodes
 * get), parcourent la liste depuis le début à l'aide d'un itérateur (renvoyé
 * par la méthode iterator) jusqu'à atteindre l'élèment à cet index, en
 * conséquence, pour des raisons d'efficacité, l'usage d'un itérateur doit être
 * préféré à chaque fois qu'un parcours de la liste doit être effecué. Les
 * méthodes iterator et listIterator construisent l'itérateur renvoyé en
 * fusionnant les itérateurs des Listes des catégories sélectionnées à l'aide
 * d'un FusionSortedIterator.
 * </p>
 *
 * @author Marc Champesme
 * @version 8/12/2024
 * @invariant (\ forall int i, j ; i > = 0 & & i < j & & j < size ();
 * get(i).compareTo(get(j)) <= 0);
 * @invariant (\ forall Set < C > catSet ; true ; < br / >
 *( \ forall int i, j ; i > = 0 & & i < j & & j < size ( catSet); <br/>
 * get(catSet, i).compareTo(get(catSet, j)) <= 0);
 * @invariant !contains(null);
 * @invariant getCatType() != null;
 * @since 27/09/2024
 */
public class MultiEnumList<C extends Enum<C>, E extends Categorized<C> & Comparable<E>> extends AbstractCollection<E>
        implements Cloneable {

        private LinkedList<E>[] Listes;
        private final Class<C> Cats;

        /**
         * Initialise une MultiEnumList vide dont les éléments sont catégorisés à l'aide
         * du type spécifié.
         *
         * @param catType le type enum permettant de catégoriser les éléments de cette
         *                MultiEnumList
         * @throws NullPointerException si l'argument spécifié est null
         * @requires catType != null;
         * @ensures isEmpty();
         * @ensures getCatType().equals(catType);
         */
        public MultiEnumList(Class<C> catType) {
                super();
                if (catType == null) {
                        throw new NullPointerException();
                }
                Cats = catType;
                 // Utilisation d'une List pour eviter les avertissements de type non verified
                Listes = (LinkedList<E>[]) new LinkedList[Cats.getEnumConstants().length];
                for (int i = 0; i < Listes.length; i++) {
                        Listes[i] = new LinkedList<>();
                }
        }

        /**
         * Initialise une MultiEnumList contenant les éléments de la collection
         * spécifiée dont les éléments sont catégorisés à l'aide du type spécifié.
         *
         * @param catType le type enum permettant de catégoriser les éléments de cette
         *                MultiEnumList
         * @param c       la collection dont les élèments doivent être placés dans cette
         *                nouvelle MultiEnumList
         * @throws NullPointerException si un des arguments spécifiés est null ou si la
         *                              collection spécifié contient null
         * @requires catType != null;
         * @requires c != null;
         * @requires !c.contains(null);
         * @ensures containsAll(c);
         * @ensures size() == c.size();
         * @ensures getCatType().equals(catType);
         */
        public MultiEnumList(Class<C> catType, Collection<? extends E> c) {

           // Appel au constructeur principal
                this(catType);
                // Verification si la collection passée en parametre est nulle
                if (c == null) {
                 throw new NullPointerException();
                }
                // Parcours
                for (E elem : c) {
                        if (elem == null) {
                         throw new NullPointerException();
                        }

                        // Ajoute l'élément actuel à l'instance de MultiEnumList
                        this.add(elem);
                }

        }

        /**
         * Renvoie le type enum catégorisant les élèments de cette collection.
         *
         * @return le type enum catégorisant les élèments de cette collection
         * @pure
         */
        public Class<C> getCatType() {
                return Cats;
        }

        @Override
        public int size() {

              //pour stocker la taille totale
                int res = 0;

                for (LinkedList<E> l : Listes) {
                        res += l.size();
                }
                return res;
        }

        /**
         * Renvoie le nombre d'élèments de cette collection appartenant à une des
         * catégories de l'ensemble spécifié.
         *
         * @param catSet ensemble de catégories
         * @return le nombre d'élèments de cette collection appartenant à une des
         * catégories de l'ensemble spécifié
         * @throws NullPointerException si l'ensemble spécifié est null ou contient null
         * @requires catSet != null;
         * @requires !catSet.contains(null)
         * @ensures \result >= 0 && \result <= size();
         * @ensures catSet.isEmpty() ==> \result == 0;
         * @ensures catSet.equals(EnumSet.allOf ( getCatType ())) ==> \result == size();
         * @pure
         */

        public int size(Set<? extends C> catSet) {

              //stocker la taille totale
                int res = 0;

             //verification
                if ((catSet == null) || (catSet.contains(null))) {
                        throw new NullPointerException();
                }

           //parcourt
                for (C cat : catSet) {
                res = res + Listes[cat.ordinal()].size();
                }

                return res;
        }

        
        @Override
        public boolean remove(Object obj) {
                // Appelle la methode eremove de la classe parente en lui passant loobjet a supprimer
                return super.remove(obj);
        }

        @Override
        public boolean contains(Object obj) {
                return super.contains(obj);
        }

        @Override
        public void clear() {
                super.clear();
        }

        /**
         * Retire tous les élèments de cette collection dont la catégorie appartient à
         * une des catégories de l'ensemble spécifié. Si l'ensemble spécifié est vide
         * cette collection n'est pas modifiée.
         *
         * @param catSet ensemble de catégories auxquelles appartiennent les éléments à
         *               retirer de cette collection
         * @throws NullPointerException si l'ensemble spécifié est null ou contient null
         * @requires catSet != null;
         * @ensures size(catSet) == 0;
         * @requires !catSet.contains(null)
         * @ensures catSet.isEmpty() ==> size() == \old(size());
         * @ensures catSet.equals(EnumSet.allOf ( getCatType ())) ==> isEmpty();
         * @ensures (\ forall E elt ; catSet.contains ( elt.getCategory ()) &&
         * \old(contains(elt)); !contains(elt));
         * @ensures (\ forall E elt ; ! catSet.contains ( elt.getCategory ()) &&
         * \old(contains(elt)); contains(elt));
         */
        public void clear(Set<? extends C> catSet) {
                // Vérifie 
                if ((catSet == null) || (catSet.contains(null))) {
                  throw new NullPointerException();
                }
          //parcourir les categories
                for (C cat : catSet) {
                 Listes[cat.ordinal()].clear();
                }
        }

        /**
         * Renvoie l'élèment situé à la position spécifiée dans cette collection.
         *
         * @param i index de l'élèmet à renvoyer
         * @return l'élèment situé à la position spécifiée dans cette collection
         * @throws IndexOutOfBoundsException si l'index spécifié est strictement
         *                                   inférieur à 0 ou supérieur ou égal à size()
         * @requires i >= 0 && i < size();
         * @ensures contains(\ result);
         * @ensures (\ forall int j ; j > = 0 & & j < i ; get ( j).compareTo(\ result) <= 0);
         * @ensures (\ forall int j ; j > i & & j < size (); get(j).compareTo(\result) >=
         * 0);
         * @pure
         */
        public E get(int i) {
               // Verifie si lindice est dehors des limites de la collection
                if ((i < 0) || (i >= size())) {
                        throw new IndexOutOfBoundsException();
                }
                // Obtient un itérateur sur la collection
                ListIterator<E> iter = iterator();
                int j = 0;
                // Parcourt 
                while (j < i) {
                        iter.next();
                        j++;
                }
             // Renvoie lement
                return iter.next();

        }

        /**
         * Renvoie l'élèment situé à la position spécifiée parmi les élèments de cette
         * collection dont la catégorie appartient à l'ensemble spécifié.
         *
         * @param i      index de l'élèmet à renvoyer
         * @param catSet ensemble des catégories
         * @return l'élèment situé à la position spécifiée parmi les élèments dont la
         * catégorie appartient à l'ensemble spécifié
         * @throws IndexOutOfBoundsException si l'index spécifié est strictement
         *                                   inférieur à 0 ou supérieur ou égal à size()
         * @throws NullPointerException      si l'ensemble spécifié est null ou contient
         *                                   null
         * @requires i >= 0 && i < size(catSet);
         * @requires !catSet.contains(null)
         * @ensures contains(\ result);
         * @ensures catSet.contains(\ result.getCategory ());
         * @ensures (\ forall int j ; j > = 0 & & j < i ; get ( catSet, j).compareTo(\ result)
         * <= 0);
         * @ensures (\ forall int j ; j > i & & j < size ( catSet); get(catSet,
         * j).compareTo(\result) >= 0);
         * @pure
         */
        public E get(Set<? extends C> catSet, int i) {

             //conditions
          if ((catSet.contains(null)) ||(catSet == null) ) {
                throw new NullPointerException();
                }
                 // Vérifie si 'i' est hors limites
           if ((i < 0) || (i >= size(catSet))) {
                throw new IndexOutOfBoundsException();
                }
                // Itérateur pour les catégories dans 'catSet'
          ListIterator<E> iter = listIterator(catSet);
          int j = 0;

                // Avance jusqu'à l'indice 'i'
           while (j < i) {
                iter.next();
                  j++;
                }
                return iter.next();
        }

        /**
         * Ajoute l'élèment spécifié à cette collection en préservant l'ordre des
         * élèments.
         *
         * @param elt l'élèment à ajouter à cette collection
         * @return toujours true
         * @throws NullPointerException si l'élèment spécifié est null
         * @requires elt != null;
         * @ensures \result == true;
         * @ensures contains(elt);
         * @ensures size() == \old(size()) + 1;
         * @ensures size(EnumSet.of ( elt.getCategory ())) ==
         * \old(size(EnumSet.of(elt.getCategory()))) + 1;
         */
        @Override
        public boolean add(E elt) {

              // Verifie si lelement est nul
                if (elt == null) {
                        throw new NullPointerException();
                }

                // Obtient la liste correspondant à la categorie de leelement
                LinkedList<E> list = Listes[elt.getCategory().ordinal()];

                 // Ajoute lelement a la liste
                ListIterator<E> iter = list.listIterator();
                while (iter.hasNext()) {
                        if (elt.compareTo(iter.next()) <= 0) {
                                iter.previous(); // Revient en arriere si la position est trouvée
                                break;
                        }
                }
                // Insère l'élément à la position correcte
                iter.add(elt);
                return true;
        }

        /**
         * Renvoie un ListIterator sur les éléments de cette collection dont la
         * catégorie appartient à l'ensemble spécifié. Cet itérateur respect l'ordre
         * naturel des élèments.
         *
         * @param catSet ensemble de catégories
         * @return un ListIterator sur les éléments de cette collection dont la
         * catégorie appartient à l'ensemble spécifié
         * @throws NullPointerException si l'ensemble spécifié est null ou contient null
         * @implSpec L'itérateur renvoyé est construit en fusionnant des itérateurs de
         * chacune des Listes des catégories sélectionnées. Aucune nouvelle
         * liste n'est crée.
         * @requires catSet != null;
         * @requires !catSet.contains(null)
         * @ensures \result != null;
         * @ensures ListIterObserverAdapter.containsAll(\ result, this);
         * @ensures ListIterObserverAdapter.size(\ result) == size(catSet);
         * @pure
         */
        public ListIterator<E> listIterator(Set<? extends C> catSet) {
                //verifie
                if ((catSet == null) || (catSet.contains(null))) {
                        throw new NullPointerException();
                }
                //  Crée une liste pour stocker les itérateurs
                ArrayList<ListIterator<E>> myList = new ArrayList<>();

                // Ajoute un itérateur pour chaque catégorie de 'catSet'
                for (C cat : catSet) {
                        myList.add(Listes[cat.ordinal()].listIterator(0));
                }
                //retourne un itérateur fusionné trié
                return new FusionSortedIterator<>(myList);
        }

        /**
         * Renvoie un ListIterator sur tous les éléments de cette collection. Cet
         * itérateur respect l'ordre naturel des élèments.
         *
         * @return un ListIterator sur les éléments de cette collection dont la
         * catégorie appartient à l'ensemble spécifié
         * @implSpec L'itérateur renvoyé est construit en fusionnant des itérateurs de
         * chacune des catégories. Aucune nouvelle liste n'est crée.
         * @requires catSet != null;
         * @ensures \result != null;
         * @ensures containsAll(ListIterObserverAdapter.toList ( \ result));
         * @ensures ListIterObserverAdapter.size(\ result) == size();
         * @ensures ListIterObserverAdapter.isSorted(\ result);
         * @pure
         */
        @Override
        public ListIterator<E> iterator() {
                // Creer une liste pour stocker les iterateurs
                ArrayList<ListIterator<E>> Liste = new ArrayList<>();
                   // Ajoute un iterateur pour chaque liste de Listes
                for (LinkedList<E> l : Listes) {
                        Liste.add(l.listIterator(0));
                }
                // Retourne un iterateur fusionné trié
                return new FusionSortedIterator<>(Liste);

        }

        /**
         * Compare l'objet spécifié avec cette collection en terme d'égalité. Renvoie
         * true si l'objet spécifié est une MultiEnumList contenant les mêmes éléments
         * dans le même ordre que cette collection.
         *
         * @param obj l'objet à comparer avec cette collection en terme d'égalité
         * @return true si l'objet spécifié est une MultiEnumList contenant les mêmes
         * éléments que cette collection
         * @ensures !(obj instanceof MultiEnumList<?,?>) ==> !\result;
         * @ensures !getCatType().equals(((MultiEnumList<?,?>) obj).getCatType()) ==>
         * !\result;
         * @ensures \result ==> size() == ((MultiEnumList<?,?>) obj).size();
         * @ensures \result ==> (\forall int i; i >= 0 && i < size();
         * get(i).equals(((MultiEnumList<?,?>) obj).get(i)));
         * @pure
         */
        @Override
        public boolean equals(Object obj) {
                // Vérifie si 'obj' est une instance de MultiEnumList
                if (!(obj instanceof MultiEnumList<?, ?> tmp)) {
                        return false;
                }
                //si les tailles sont differentes
                if (size() != tmp.size()) {
                        return false;
                }
                // Vérifie si les ensembles de catégories sont égaux
                if (!(Cats.equals(tmp.Cats))) {
                        return false;
                }
                // Compare les éléments un par un
                for (int i = 0; i < size(); i++) {
                        if (!get(i).equals(tmp.get(i))) {
                                return false;
                        }
                }
                // Si toutes les vérifications passent, les deux Listes sont égales
                return true;
        }

        /**
         * Returns the hash code value for this MultiEnumList. The hash code of a
         * MultiEnumList is defined to be the result of the following calculation:
         *
         * <pre>{@code
         * int hashCode = 1;
         * for (E e : list)
         * 	hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
         * }</pre>
         *
         * @return the hash code value for this list
         * @pure
         */
        @Override
        public int hashCode() {
                int hashCode = 1;
                for (E e : this) {
                        hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
                }
                return hashCode;
        }

        /**
         * Renvoie un clone de cette MultiEnumList. Chacune des Listes composant cette
         * MultiEnumList est clonée.
         *
         * @return un clone de cette MultiEnumList
         * @ensures \result.getClass().equals(getClass());
         * @ensures \result != this;
         * @ensures \result.equals(this);
         * @pure
         */
        @Override
        public MultiEnumList<C, E> clone() {

                MultiEnumList<C, E> LeClone;
                try {
                        // Clone lobjet en appelant la methode clone de la classe parente
                        LeClone = (MultiEnumList<C, E>) super.clone();
                } catch (CloneNotSupportedException e) {
                         // erreur interne si le clonage echoue
                        throw new InternalError();
                }
                // Clone le tableau 'Listes'
                LeClone.Listes = Listes.clone();

                // Clone chaque liste individuelle dans 'Listes'
                for (int i = 0; i < Listes.length; i++) {
                        
                        LeClone.Listes[i] = (LinkedList<E>) this.Listes[i].clone();
                }
                 // Retourne le clone entièrement copié
                return LeClone;

        }
}


