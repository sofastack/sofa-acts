/**
 * Copyright (c) 2008-2013, http://www.snakeyaml.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.yaml.serializer;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alipay.yaml.DumperOptions;
import com.alipay.yaml.DumperOptions.Version;
import com.alipay.yaml.emitter.Emitable;
import com.alipay.yaml.events.AliasEvent;
import com.alipay.yaml.events.DocumentEndEvent;
import com.alipay.yaml.events.DocumentStartEvent;
import com.alipay.yaml.events.ImplicitTuple;
import com.alipay.yaml.events.MappingEndEvent;
import com.alipay.yaml.events.MappingStartEvent;
import com.alipay.yaml.events.ScalarEvent;
import com.alipay.yaml.events.SequenceEndEvent;
import com.alipay.yaml.events.SequenceStartEvent;
import com.alipay.yaml.events.StreamEndEvent;
import com.alipay.yaml.events.StreamStartEvent;
import com.alipay.yaml.nodes.AnchorNode;
import com.alipay.yaml.nodes.CollectionNode;
import com.alipay.yaml.nodes.MappingNode;
import com.alipay.yaml.nodes.Node;
import com.alipay.yaml.nodes.NodeId;
import com.alipay.yaml.nodes.NodeTuple;
import com.alipay.yaml.nodes.ScalarNode;
import com.alipay.yaml.nodes.SequenceNode;
import com.alipay.yaml.nodes.Tag;
import com.alipay.yaml.resolver.Resolver;

public final class Serializer {
    private final Emitable      emitter;
    private final Resolver      resolver;
    private boolean             explicitStart;
    private boolean             explicitEnd;
    private Version             useVersion;
    private Map<String, String> useTags;
    private Set<Node>           serializedNodes;
    private Map<Node, String>   anchors;
    private int                 lastAnchorId;
    private Boolean             closed;
    private Tag                 explicitRoot;

    /**
     * Constructor.
     *
     * @param emitter the emitter
     * @param resolver the resolver
     * @param opts the opts
     * @param rootTag the root tag
     */
    public Serializer(Emitable emitter, Resolver resolver, DumperOptions opts, Tag rootTag) {
        this.emitter = emitter;
        this.resolver = resolver;
        this.explicitStart = opts.isExplicitStart();
        this.explicitEnd = opts.isExplicitEnd();
        if (opts.getVersion() != null) {
            this.useVersion = opts.getVersion();
        }
        this.useTags = opts.getTags();
        this.serializedNodes = new HashSet<Node>();
        this.anchors = new HashMap<Node, String>();
        this.lastAnchorId = 0;
        this.closed = null;
        this.explicitRoot = rootTag;
    }

    /**
     * Open.
     *
     * @throws IOException the io exception
     */
    public void open() throws IOException {
        if (closed == null) {
            this.emitter.emit(new StreamStartEvent(null, null));
            this.closed = Boolean.FALSE;
        } else if (Boolean.TRUE.equals(closed)) {
            throw new SerializerException("serializer is closed");
        } else {
            throw new SerializerException("serializer is already opened");
        }
    }

    /**
     * Close.
     *
     * @throws IOException the io exception
     */
    public void close() throws IOException {
        if (closed == null) {
            throw new SerializerException("serializer is not opened");
        } else if (!Boolean.TRUE.equals(closed)) {
            this.emitter.emit(new StreamEndEvent(null, null));
            this.closed = Boolean.TRUE;
        }
    }

    /**
     * Serialize.
     *
     * @param node the node
     * @throws IOException the io exception
     */
    public void serialize(Node node) throws IOException {
        if (closed == null) {
            throw new SerializerException("serializer is not opened");
        } else if (closed) {
            throw new SerializerException("serializer is closed");
        }
        this.emitter.emit(new DocumentStartEvent(null, null, this.explicitStart, this.useVersion,
            useTags));
        anchorNode(node);
        if (explicitRoot != null) {
            node.setTag(explicitRoot);
        }
        serializeNode(node, null);
        this.emitter.emit(new DocumentEndEvent(null, null, this.explicitEnd));
        this.serializedNodes.clear();
        this.anchors.clear();
        this.lastAnchorId = 0;
    }

    private void anchorNode(Node node) {
        if (node.getNodeId() == NodeId.anchor) {
            node = ((AnchorNode) node).getRealNode();
        }
        if (this.anchors.containsKey(node)) {
            String anchor = this.anchors.get(node);
            if (null == anchor) {
                anchor = generateAnchor();
                this.anchors.put(node, anchor);
            }
        } else {
            this.anchors.put(node, null);
            switch (node.getNodeId()) {
                case sequence:
                    SequenceNode seqNode = (SequenceNode) node;
                    List<Node> list = seqNode.getValue();
                    for (Node item : list) {
                        anchorNode(item);
                    }
                    break;
                case mapping:
                    MappingNode mnode = (MappingNode) node;
                    List<NodeTuple> map = mnode.getValue();
                    for (NodeTuple object : map) {
                        Node key = object.getKeyNode();
                        Node value = object.getValueNode();
                        anchorNode(key);
                        anchorNode(value);
                    }
                    break;
            }
        }
    }

    private String generateAnchor() {
        this.lastAnchorId++;
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumIntegerDigits(3);
        format.setMaximumFractionDigits(0);// issue 172
        format.setGroupingUsed(false);
        String anchorId = format.format(this.lastAnchorId);
        return "id" + anchorId;
    }

    private void serializeNode(Node node, Node parent) throws IOException {
        if (node.getNodeId() == NodeId.anchor) {
            node = ((AnchorNode) node).getRealNode();
        }
        String tAlias = this.anchors.get(node);
        if (this.serializedNodes.contains(node)) {
            this.emitter.emit(new AliasEvent(tAlias, null, null));
        } else {
            this.serializedNodes.add(node);
            switch (node.getNodeId()) {
                case scalar:
                    ScalarNode scalarNode = (ScalarNode) node;
                    Tag detectedTag = this.resolver.resolve(NodeId.scalar, scalarNode.getValue(),
                        true);
                    Tag defaultTag = this.resolver.resolve(NodeId.scalar, scalarNode.getValue(),
                        false);
                    ImplicitTuple tuple = new ImplicitTuple(node.getTag().equals(detectedTag), node
                        .getTag().equals(defaultTag));
                    ScalarEvent event = new ScalarEvent(tAlias, node.getTag().getValue(), tuple,
                        scalarNode.getValue(), null, null, scalarNode.getStyle());
                    this.emitter.emit(event);
                    break;
                case sequence:
                    SequenceNode seqNode = (SequenceNode) node;
                    boolean implicitS = (node.getTag().equals(this.resolver.resolve(
                        NodeId.sequence, null, true)));
                    this.emitter.emit(new SequenceStartEvent(tAlias, node.getTag().getValue(),
                        implicitS, null, null, seqNode.getFlowStyle()));
                    int indexCounter = 0;
                    List<Node> list = seqNode.getValue();
                    for (Node item : list) {
                        serializeNode(item, node);
                        indexCounter++;
                    }
                    this.emitter.emit(new SequenceEndEvent(null, null));
                    break;
                default:// instance of MappingNode
                    Tag implicitTag = this.resolver.resolve(NodeId.mapping, null, true);
                    boolean implicitM = (node.getTag().equals(implicitTag));
                    this.emitter.emit(new MappingStartEvent(tAlias, node.getTag().getValue(),
                        implicitM, null, null, ((CollectionNode) node).getFlowStyle()));
                    MappingNode mnode = (MappingNode) node;
                    List<NodeTuple> map = mnode.getValue();
                    for (NodeTuple row : map) {
                        Node key = row.getKeyNode();
                        Node value = row.getValueNode();
                        serializeNode(key, mnode);
                        serializeNode(value, mnode);
                    }
                    this.emitter.emit(new MappingEndEvent(null, null));
            }
        }
    }
}
