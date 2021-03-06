/*
 * #%L
 * jovial
 * %%
 * Copyright (C) 2016 Smithsonian Astrophysical Observatory
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Smithsonian Astrophysical Observatory nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package cfa.vo.vodml.gui.tree

import ca.odell.glazedlists.EventList

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

class ListTreeNode<T> implements TreeNode {
    String name
    def parent
    EventList<T> userObject

    public ListTreeNode(String name, EventList<T> userObject, parent) {
        this.userObject = userObject
        userObject.addListEventListener {
            if(parent) parent.listChanged()
        }
        this.name = name
        this.parent = parent
    }

    public String toString() {
        return "${name}s"
    }

    TreeNode getChildAt(int childIndex) {
        return new DefaultMutableTreeNode(userObject.get(childIndex))
    }

    def menuItems = { swing ->
        swing.menuItem(label: "Add ${name}")
    }

    @Override
    int getChildCount() {
        return userObject.size()
    }

    @Override
    TreeNode getParent() {
        return parent
    }

    @Override
    int getIndex(TreeNode node) {
        return userObject.indexOf(node.userObject)
    }

    @Override
    boolean getAllowsChildren() {
        return true
    }

    @Override
    boolean isLeaf() {
        return userObject.isEmpty()
    }

    @Override
    Enumeration children() {
        throw new UnsupportedOperationException()
    }
}
