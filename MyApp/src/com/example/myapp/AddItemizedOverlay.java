package com.example.myapp;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class AddItemizedOverlay extends ItemizedOverlay<OverlayItem>{

	private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
	Context context;
	
	public AddItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public AddItemizedOverlay(Drawable defaultMarker, Context context){
		super(boundCenterBottom(defaultMarker));
		this.context = context;
	}

	public void addOverlay(OverlayItem overlay){
		mapOverlays.add(overlay);
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return mapOverlays.get(i);
	}

	@Override
	public int size() {
		return mapOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mapOverlays.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  return true;
	}

}
