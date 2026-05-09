package org.dsgroup.journeycraft.navigation.cache;

import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.RoadEdge;
import org.dsgroup.journeycraft.navigation.entity.RoadNode;
import org.dsgroup.journeycraft.navigation.mapper.RoadEdgeMapper;
import org.dsgroup.journeycraft.navigation.mapper.RoadNodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GraphCacheService {

    @Autowired
    private RoadEdgeMapper roadEdgeMapper;

    @Autowired
    private RoadNodeMapper roadNodeMapper;

    /** Full adjacency list: fromNodeId -> outgoing edges */
    private volatile Map<Long, List<RoadEdge>> adjacencyList;

    /** Node coordinates cache: nodeId -> [latitude, longitude] */
    private volatile Map<Long, double[]> nodeCoords;

    /** Whether the cache has been initialized */
    private volatile boolean initialized = false;

    @PostConstruct
    public synchronized void init() {
        if (initialized) return;
        long start = System.currentTimeMillis();

        // Load all enabled, non-deleted edges
        List<RoadEdge> allEdges = roadEdgeMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RoadEdge>()
                .eq(RoadEdge::getDeleted, false)
                .eq(RoadEdge::getEnabled, true)
        );

        adjacencyList = allEdges.stream()
            .collect(Collectors.groupingBy(RoadEdge::getFromNodeId));

        // Add reverse entries for bidirectional edges
        for (RoadEdge edge : allEdges) {
            if (Boolean.TRUE.equals(edge.getBidirectional())) {
                adjacencyList.computeIfAbsent(edge.getToNodeId(), k -> new ArrayList<>()).add(edge);
            }
        }

        // Load all node coordinates
        List<RoadNode> allNodes = roadNodeMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RoadNode>()
                .select(RoadNode::getId, RoadNode::getLatitude, RoadNode::getLongitude)
                .eq(RoadNode::getDeleted, false)
                .eq(RoadNode::getEnabled, true)
        );

        nodeCoords = new HashMap<>(allNodes.size());
        for (RoadNode node : allNodes) {
            if (node.getLatitude() != null && node.getLongitude() != null) {
                nodeCoords.put(node.getId(), new double[]{
                    node.getLatitude().doubleValue(),
                    node.getLongitude().doubleValue()
                });
            }
        }

        initialized = true;
        long elapsed = System.currentTimeMillis() - start;
        log.info("GraphCacheService 初始化完成: {} 节点 ({} 有出边), {} 边, {} 坐标缓存, 耗时 {}ms",
            allNodes.size(), adjacencyList.size(), allEdges.size(), nodeCoords.size(), elapsed);
    }

    /** Get the full adjacency list (null if not initialized) */
    public Map<Long, List<RoadEdge>> getAdjacencyList() {
        return adjacencyList;
    }

    /** Get edges for a specific node */
    public List<RoadEdge> getEdges(Long fromNodeId) {
        if (adjacencyList == null) return Collections.emptyList();
        return adjacencyList.getOrDefault(fromNodeId, Collections.emptyList());
    }

    /** Get coordinates for a node [lat, lng] or null */
    public double[] getNodeCoord(Long nodeId) {
        if (nodeCoords == null) return null;
        return nodeCoords.get(nodeId);
    }

    /** Check if cache is ready */
    public boolean isInitialized() {
        return initialized;
    }
}
