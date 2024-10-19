/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
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
package com.baomidou.mybatisplus.extension.parser.cache;

import org.nustaq.serialization.FSTConfiguration;

/**
 * Fst Factory
 *
 * @author miemie
 * @since 2023-08-06
 */
public class FstFactory {
    private static final FstFactory FACTORY = new FstFactory();
    private final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    public static FstFactory getDefaultFactory() {
        return FACTORY;
    }

    public FstFactory() {
        conf.registerClass(net.sf.jsqlparser.expression.Alias.class);
        conf.registerClass(net.sf.jsqlparser.expression.Alias.AliasColumn.class);
        conf.registerClass(net.sf.jsqlparser.expression.AllValue.class);
        conf.registerClass(net.sf.jsqlparser.expression.AnalyticExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.AnyComparisonExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.ArrayConstructor.class);
        conf.registerClass(net.sf.jsqlparser.expression.ArrayExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.CaseExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.CastExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.CollateExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.ConnectByRootOperator.class);
        conf.registerClass(net.sf.jsqlparser.expression.DateTimeLiteralExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.DateValue.class);
        conf.registerClass(net.sf.jsqlparser.expression.DoubleValue.class);
        conf.registerClass(net.sf.jsqlparser.expression.ExtractExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.FilterOverImpl.class);
        conf.registerClass(net.sf.jsqlparser.expression.Function.class);
        conf.registerClass(net.sf.jsqlparser.expression.Function.HavingClause.class);
        conf.registerClass(net.sf.jsqlparser.expression.HexValue.class);
        conf.registerClass(net.sf.jsqlparser.expression.IntervalExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.JdbcNamedParameter.class);
        conf.registerClass(net.sf.jsqlparser.expression.JdbcParameter.class);
        conf.registerClass(net.sf.jsqlparser.expression.JsonAggregateFunction.class);
        conf.registerClass(net.sf.jsqlparser.expression.JsonExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.JsonFunction.class);
        conf.registerClass(net.sf.jsqlparser.expression.JsonFunctionExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.JsonKeyValuePair.class);
        conf.registerClass(net.sf.jsqlparser.expression.KeepExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.LambdaExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.LongValue.class);
        conf.registerClass(net.sf.jsqlparser.expression.MySQLGroupConcat.class);
        conf.registerClass(net.sf.jsqlparser.expression.MySQLIndexHint.class);
        conf.registerClass(net.sf.jsqlparser.expression.NextValExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.NotExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.NullValue.class);
        conf.registerClass(net.sf.jsqlparser.expression.NumericBind.class);
        conf.registerClass(net.sf.jsqlparser.expression.OracleHierarchicalExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.OracleHint.class);
        conf.registerClass(net.sf.jsqlparser.expression.OracleNamedFunctionParameter.class);
        conf.registerClass(net.sf.jsqlparser.expression.OrderByClause.class);
        conf.registerClass(net.sf.jsqlparser.expression.OverlapsCondition.class);
        conf.registerClass(net.sf.jsqlparser.expression.Parenthesis.class);
        conf.registerClass(net.sf.jsqlparser.expression.PartitionByClause.class);
        conf.registerClass(net.sf.jsqlparser.expression.RangeExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.RowConstructor.class);
        conf.registerClass(net.sf.jsqlparser.expression.RowGetExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.SQLServerHints.class);
        conf.registerClass(net.sf.jsqlparser.expression.SignedExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.StringValue.class);
        conf.registerClass(net.sf.jsqlparser.expression.StructType.class);
        conf.registerClass(net.sf.jsqlparser.expression.TimeKeyExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.TimeValue.class);
        conf.registerClass(net.sf.jsqlparser.expression.TimestampValue.class);
        conf.registerClass(net.sf.jsqlparser.expression.TimezoneExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.TranscodingFunction.class);
        conf.registerClass(net.sf.jsqlparser.expression.TrimFunction.class);
        conf.registerClass(net.sf.jsqlparser.expression.UserVariable.class);
        conf.registerClass(net.sf.jsqlparser.expression.VariableAssignment.class);
        conf.registerClass(net.sf.jsqlparser.expression.WhenClause.class);
        conf.registerClass(net.sf.jsqlparser.expression.WindowDefinition.class);
        conf.registerClass(net.sf.jsqlparser.expression.WindowElement.class);
        conf.registerClass(net.sf.jsqlparser.expression.WindowOffset.class);
        conf.registerClass(net.sf.jsqlparser.expression.WindowRange.class);
        conf.registerClass(net.sf.jsqlparser.expression.XMLSerializeExpr.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.arithmetic.Addition.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseLeftShift.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseRightShift.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.arithmetic.Concat.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.arithmetic.Division.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.arithmetic.IntegerDivision.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.arithmetic.Modulo.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.arithmetic.Multiplication.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.arithmetic.Subtraction.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.conditional.AndExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.conditional.OrExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.conditional.XorExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.Between.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.ContainedBy.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.Contains.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.DoubleAnd.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.EqualsTo.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.ExcludesExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.ExistsExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.ExpressionList.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.FullTextSearch.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.GeometryDistance.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.GreaterThan.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.InExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.IncludesExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.IsBooleanExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.IsDistinctExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.IsNullExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.JsonOperator.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.LikeExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.Matches.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.MemberOfExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.MinorThan.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.MinorThanEquals.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.NamedExpressionList.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.NotEqualsTo.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.SimilarToExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.TSQLLeftJoin.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.TSQLRightJoin.class);
        conf.registerClass(net.sf.jsqlparser.parser.ASTNodeAccessImpl.class);
        conf.registerClass(net.sf.jsqlparser.parser.Token.class);
        conf.registerClass(net.sf.jsqlparser.schema.Column.class);
        conf.registerClass(net.sf.jsqlparser.schema.Sequence.class);
        conf.registerClass(net.sf.jsqlparser.schema.Synonym.class);
        conf.registerClass(net.sf.jsqlparser.schema.Table.class);
        conf.registerClass(net.sf.jsqlparser.statement.Block.class);
        conf.registerClass(net.sf.jsqlparser.statement.Commit.class);
        conf.registerClass(net.sf.jsqlparser.statement.DeclareStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.DeclareStatement.TypeDefExpr.class);
        conf.registerClass(net.sf.jsqlparser.statement.DescribeStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.ExplainStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.ExplainStatement.Option.class);
        conf.registerClass(net.sf.jsqlparser.statement.IfElseStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.OutputClause.class);
        conf.registerClass(net.sf.jsqlparser.statement.PurgeStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.ReferentialAction.class);
        conf.registerClass(net.sf.jsqlparser.statement.ResetStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.RollbackStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.SavepointStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.SetStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.ShowColumnsStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.ShowStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.Statements.class);
        conf.registerClass(net.sf.jsqlparser.statement.UnsupportedStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.UseStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.alter.Alter.class);
        conf.registerClass(net.sf.jsqlparser.statement.alter.AlterExpression.class);
        conf.registerClass(net.sf.jsqlparser.statement.alter.AlterExpression.ColumnDataType.class);
        conf.registerClass(net.sf.jsqlparser.statement.alter.AlterExpression.ColumnDropDefault.class);
        conf.registerClass(net.sf.jsqlparser.statement.alter.AlterExpression.ColumnDropNotNull.class);
        conf.registerClass(net.sf.jsqlparser.statement.alter.AlterSession.class);
        conf.registerClass(net.sf.jsqlparser.statement.alter.AlterSystemStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.alter.RenameTableStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.alter.sequence.AlterSequence.class);
        conf.registerClass(net.sf.jsqlparser.statement.analyze.Analyze.class);
        conf.registerClass(net.sf.jsqlparser.statement.comment.Comment.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.function.CreateFunction.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.index.CreateIndex.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.procedure.CreateProcedure.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.schema.CreateSchema.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.sequence.CreateSequence.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.synonym.CreateSynonym.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.table.CheckConstraint.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.table.ColDataType.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.table.ColumnDefinition.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.table.CreateTable.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.table.ExcludeConstraint.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.table.ForeignKeyIndex.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.table.Index.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.table.Index.ColumnParams.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.table.NamedConstraint.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.table.RowMovement.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.view.AlterView.class);
        conf.registerClass(net.sf.jsqlparser.statement.create.view.CreateView.class);
        conf.registerClass(net.sf.jsqlparser.statement.delete.Delete.class);
        conf.registerClass(net.sf.jsqlparser.statement.drop.Drop.class);
        conf.registerClass(net.sf.jsqlparser.statement.execute.Execute.class);
        conf.registerClass(net.sf.jsqlparser.statement.grant.Grant.class);
        conf.registerClass(net.sf.jsqlparser.statement.insert.Insert.class);
        conf.registerClass(net.sf.jsqlparser.statement.insert.InsertConflictAction.class);
        conf.registerClass(net.sf.jsqlparser.statement.insert.InsertConflictTarget.class);
        conf.registerClass(net.sf.jsqlparser.statement.merge.Merge.class);
        conf.registerClass(net.sf.jsqlparser.statement.merge.MergeDelete.class);
        conf.registerClass(net.sf.jsqlparser.statement.merge.MergeInsert.class);
        conf.registerClass(net.sf.jsqlparser.statement.merge.MergeUpdate.class);
        conf.registerClass(net.sf.jsqlparser.statement.refresh.RefreshMaterializedViewStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.AllColumns.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.AllTableColumns.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.Distinct.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.ExceptOp.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.Fetch.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.First.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.ForClause.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.GroupByElement.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.IntersectOp.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.Join.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.KSQLJoinWindow.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.KSQLWindow.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.LateralSubSelect.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.LateralView.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.Limit.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.MinusOp.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.Offset.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.OptimizeFor.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.OrderByElement.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.ParenthesedFromItem.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.ParenthesedSelect.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.Pivot.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.PivotXml.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.PlainSelect.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.SelectItem.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.SetOperationList.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.Skip.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.TableFunction.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.TableStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.Top.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.UnPivot.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.UnionOp.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.Values.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.Wait.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.WithIsolation.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.WithItem.class);
        conf.registerClass(net.sf.jsqlparser.statement.show.ShowIndexStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.show.ShowTablesStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.truncate.Truncate.class);
        conf.registerClass(net.sf.jsqlparser.statement.update.Update.class);
        conf.registerClass(net.sf.jsqlparser.statement.update.UpdateSet.class);
        conf.registerClass(net.sf.jsqlparser.statement.upsert.Upsert.class);
        conf.registerClass(net.sf.jsqlparser.util.cnfexpression.MultiAndExpression.class);
        conf.registerClass(net.sf.jsqlparser.util.cnfexpression.MultiOrExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.BinaryExpression.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.ComparisonOperator.class);
        conf.registerClass(net.sf.jsqlparser.expression.operators.relational.OldOracleJoinBinaryExpression.class);
        conf.registerClass(net.sf.jsqlparser.statement.CreateFunctionalStatement.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.Select.class);
        conf.registerClass(net.sf.jsqlparser.statement.select.SetOperation.class);
        conf.registerClass(net.sf.jsqlparser.util.cnfexpression.MultipleExpression.class);
    }

    public byte[] asByteArray(Object obj) {
        return conf.asByteArray(obj);
    }

    public Object asObject(byte[] bytes) {
        return conf.asObject(bytes);
    }
}
