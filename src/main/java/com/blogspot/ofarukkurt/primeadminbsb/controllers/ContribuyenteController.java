package com.blogspot.ofarukkurt.primeadminbsb.controllers;


import com.blogspot.ofarukkurt.primeadminbsb.models.Contribuyente;
import com.blogspot.ofarukkurt.primeadminbsb.services.ContribuyenteFacade;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author Omer Faruk KURT
 * @mail kurtomerfaruk@gmail.com
 * @blog : https://ofarukkurt.blogspot.com.tr/
 * @Created on date 27.01.2017 23:11:05
 */
@Named(value = "contribuyenteController")
@ViewScoped
public class ContribuyenteController extends AbstractController<Contribuyente> {

    private static final long serialVersionUID = -3404882713320828831L;

    @EJB
    private ContribuyenteFacade ContribuyenteService;

    

    private List<String> iconList;
    private List<Contribuyente> ContribuyenteList;

    private String pageLink;
    private String pageName;

    private String searhText;

    public ContribuyenteController() {
        // Inform the Abstract parent controller of the concrete Contribuyente Entity
        super(Contribuyente.class);
        iconList = new ArrayList<>();
        String str = "data_usage,search,person_pin,directions_car,art_track,apps,poll,Contribuyente,"
                + "credit_card,attach_money,insert_drive_file,public,insert_photo,contacts";
        iconList.addAll(Arrays.asList(str.split("\\s*,\\s*")));
        Collections.sort(iconList);
        pageLink = "blankPage";
        pageName = "Main Page";
    }





    public List<String> getIconList() {
        return iconList;
    }

    public void setIconList(List<String> iconList) {
        this.iconList = iconList;
    }

    public List<Contribuyente> getContribuyenteList() {
         if (ContribuyenteList == null) {
            ContribuyenteList = ContribuyenteService.findAll();
        }
        return ContribuyenteList;
    }

    public void setContribuyenteList(List<Contribuyente> ContribuyenteList) {
        this.ContribuyenteList = ContribuyenteList;
    }

    public String getPageLink() {
        return pageLink;
    }

    public void setPageLink(String pageLink) {
        this.pageLink = pageLink;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getSearhText() {
        return searhText;
    }

    public void setSearhText(String searhText) {
        this.searhText = searhText;
    }

    public List<String> completeIcon(String query) {
        List<String> filteredIcons = new ArrayList<String>();

        for (String icon : iconList) {
            if (icon.toLowerCase().contains(query)) {
                filteredIcons.add(icon);
            }
        }

        return filteredIcons;
    }

    @PostConstruct
    public void init() {
        if (ContribuyenteList == null) {
            ContribuyenteList = new ArrayList<>();
        }
        ContribuyenteList = ContribuyenteService.findAll();
    }

    public void setPage(String link, String name) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, Object> map = context.getViewRoot().getViewMap();
        List<String> list = new ArrayList<>();

        for (String key : map.keySet()) {
            if (!key.equals("contribuyenteController")) {
                list.add(key);
            }
        }

        if (list != null && !list.isEmpty()) {
            for (String get : list) {
                map.remove(get);
            }
        }
        setPageLink(link);
        setPageName(name);
    }

    public void ContribuyenteSearchValueChange(ValueChangeEvent event) {
        if (event.getOldValue() == null || !event.getOldValue().equals(event.getNewValue())) {
            ContribuyenteList = ContribuyenteService.searchContribuyenteList(event.getNewValue().toString());
        }

        for (int i = 0; i < ContribuyenteList.size(); i++) {
            Contribuyente get = ContribuyenteList.get(i);
            if (get.getIdContribuyente()!= null) {
                Contribuyente topContribuyente = ContribuyenteService.getTopContribuyente(get.getIdContribuyente());

                if (topContribuyente != null) {
                    if (!ContribuyenteList.contains(topContribuyente)) {
                        ContribuyenteList.add(topContribuyente);
                    }
                }
            }
        }
    }

}
