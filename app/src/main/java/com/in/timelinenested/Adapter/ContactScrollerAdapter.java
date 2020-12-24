package com.in.timelinenested.Adapter;

import android.util.Log;

import com.in.timelinenested.bean.User;
import com.in.timelinenested.bean.User_virtual;
import com.in.timelinenested.mock.Contact;
import com.in.timelinenested.mock.Section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cdflynn.android.library.scroller.SectionScrollAdapter;

public class ContactScrollerAdapter implements SectionScrollAdapter {

    private List<Section> mSections;
    private List<User> users;
    private List<User_virtual> user_virtuals;

//    public ContactScrollerAdapter(List<Contact> contacts) {
//        initWithContacts(contacts);
//    }
    public void setUsers(List<User> users){
        initWithContacts(users);
    }



    @Override
    public int getSectionCount() {
        return mSections.size();
    }

    @Override
    public String getSectionTitle(int position) {
        return mSections.get(position).getTitle();
    }

    @Override
    public int getSectionWeight(int position) {
        return mSections.get(position).getWeight();
    }

    public Section fromSectionIndex( int sectionIndex) {
        return mSections.get(sectionIndex);
    }

    public Section fromItemIndex(int itemIndex) {
        for (Section s : mSections) {
            final int range = s.getIndex() + s.getWeight();
            if (itemIndex < range) {
                return s;
            }
        }
        return mSections.get(mSections.size());
    }

    public int positionFromSection(int sectionIndex) {
        return mSections.get(sectionIndex).getIndex();
    }

    public int sectionFromPosition(int positionIndex) {
        Section s = null;
        for (int i = 0; i < mSections.size(); i++) {
            s = mSections.get(i);
            final int range = s.getIndex() + s.getWeight();
            if (positionIndex < range) {
                return i;
            }
        }
        return mSections.size() - 1;
    }

    private void initWithContacts(List<User> users) {
        //mContacts = contacts;
        this.users=users;
        mSections = new ArrayList<>();
        Collections.sort(users, User.COMPARATOR);
        String sectionTitle = null;
        User user;
        int itemCount = 0;
        for (int i = 0; i < users.size(); i++) {
            user=users.get(i);
            //contact = mContacts.get(i);
            String firstLetter = user.getNickname().substring(0, 1);
            Log.i("contactscroll", "initWithContacts: "+user.getNickname());

            if (sectionTitle == null) {
                sectionTitle = firstLetter;
            }
            if (sectionTitle.compareTo(firstLetter) == 0) {
                itemCount++;
                continue;
            }

            mSections.add(new Section(i - itemCount, sectionTitle, itemCount));
            sectionTitle = firstLetter;
            itemCount = 1;
        }
    }
}
