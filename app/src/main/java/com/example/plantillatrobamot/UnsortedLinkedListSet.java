package com.example.plantillatrobamot;

public class UnsortedLinkedListSet<E> {
    //Classe Node
    private class Node<E>{
        private E elem;
        private Node next;
        //Constructor de Node
        private Node(E element,Node n){
            elem = element;
            next=n;
        }

    }
    //Primer Node
    private Node first;
    //Constructor
    public UnsortedLinkedListSet(){
        first=null;
    }

    //Mètode per comprobar si la llista es buida
    public boolean isEmpty(){
        return first==null;
    }

    //Mètode per comprobar si la llista conté un element
    public boolean contains(E elem){
        Node p = first;
        boolean trobat =false;

        while(p !=null && !trobat){
            trobat = p.elem.equals(elem);
            p = p.next;
        }

        return trobat;
    }

    //Mètode per afegir un element nou
    public boolean add(E elem){
        boolean trobat = contains(elem);
        if (!trobat){
            Node n= new Node(elem,first);
            first=n;
        }
        return !trobat;
    }

    //Mètode per eliminar un element del conjunt
    public boolean remove(E elem){
        Node p = first; Node pp = null; boolean trobat = false;
        while(p != null && !trobat){
            trobat =p.elem.equals(elem);
            if (!trobat){
                pp=p;
                p=p.next;
            }
        }

        if(trobat){
            if(pp==null){
                first=p.next;
            }else{
                pp.next = p.next;
            }
        }
        return trobat;
    }



}
