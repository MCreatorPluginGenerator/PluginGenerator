Blockly.ContextMenuRegistry.registry.register({
  id: 'copy_selected_block_as_xml',
  weight: 250,
  displayText: function (scope) {
    return 'Copy As XML';
  },
  preconditionFn: function (scope) {
    return scope.block && !scope.block.isShadow() ? 'enabled' : 'hidden';
  },
callback: function (scope) {
    const block = scope.block;
    if (!block) return;

    const dom = Blockly.Xml.blockToDom(block);
    const next = dom.querySelector('next');
    if (next) next.remove();

    const withIds = dom.querySelectorAll('[id]');
    for (let i = 0; i < withIds.length; i++) {
        withIds[i].removeAttribute('id');
    }
    if (dom.hasAttribute('id')) {
        dom.removeAttribute('id');
    }

    const serializer = new XMLSerializer();
    let xmlText = serializer.serializeToString(dom);

    plugingenerator.setClipboard(plugingenerator.beautifyXml(xmlText));
},
  scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK,
});

Blockly.ContextMenuRegistry.registry.register({
  id: 'upload_block_name_and_copy_color',
  weight: 250,
  displayText: function (scope) {
    return 'Copy Color';
  },
  preconditionFn: function (scope) {
    return scope.block && !scope.block.isShadow() ? 'enabled' : 'hidden';
  },
  callback: function (scope) {
    const block = scope.block;
    if (!block) return;
    const blockType = block.type;
    plugingenerator.uploadBlockNameAndCopyColor(blockType);
  },
  scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK,
});