Blockly.ContextMenuRegistry.registry.register({
  id: 'copy_selected_block_as_xml',
  weight: 250,
  displayText: function (scope) {
    return 'Copy To XML';
  },
  preconditionFn: function (scope) {
    return scope.block && !scope.block.isShadow() ? 'enabled' : 'hidden';
  },
callback: function (scope) {
    const block = scope.block;
    if (!block) return;

    const dom = Blockly.Xml.blockToDom(block);

    // 移除可能存在的 next 节点（如有必要）
    const next = dom.querySelector('next');
    if (next) next.remove();

    // 清除所有 id 属性
    const withIds = dom.querySelectorAll('[id]');
    for (let i = 0; i < withIds.length; i++) {
        withIds[i].removeAttribute('id');
    }
    if (dom.hasAttribute('id')) {
        dom.removeAttribute('id');
    }

    const serializer = new XMLSerializer();
    let xmlText = serializer.serializeToString(dom);

    devUtils.setClipboard(devUtils.beautifyXml(xmlText));
},
  scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK,
});